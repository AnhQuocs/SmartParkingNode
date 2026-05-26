/**
 * rfid_reader.h — Module đọc thẻ RFID RC522
 *
 * PHẦN CỨNG — SPI mặc định ESP32:
 *   RC522 VCC  → 3.3V (KHÔNG dùng 5V!)
 *   RC522 GND  → GND
 *   RC522 SDA  → GPIO 5  (SS/CS)  ← định nghĩa trong config.h
 *   RC522 SCK  → GPIO 18 (SPI CLK)  ← NHỚ dời IR1 sang GPIO khác!
 *   RC522 MOSI → GPIO 23
 *   RC522 MISO → GPIO 19           ← NHỚ dời IR2 sang GPIO khác!
 *   RC522 RST  → GPIO 4            ← định nghĩa trong config.h
 *   RC522 IRQ  → Không nối
 *
 * ⚠️  CONFLICT GPIO:
 *   GPIO 18 (SCK) và GPIO 19 (MISO) đang dùng cho IR1, IR2.
 *   Phải đổi PIN_IR1 và PIN_IR2 trong config.h sang GPIO khác.
 *   Khuyến nghị: PIN_IR1 = 25, PIN_IR2 = 26
 *
 * THƯ VIỆN CẦN CÀI:
 *   Arduino IDE > Tools > Manage Libraries → tìm "MFRC522" by GithubCommunity
 *
 * CÁCH DÙNG:
 *   RfidReader rfid;
 *   rfid.begin();
 *   rfid.update();   // gọi trong loop() — tự ghi vào g_vehicleId
 */

#ifndef RFID_READER_H
#define RFID_READER_H

#include <Arduino.h>
#include <SPI.h>
#include <MFRC522.h>
#include "config.h"

// ─────────────────────────────────────────────
//  BIẾN CHIA SẺ — main.cpp đọc để đẩy Firebase
//  (đã khai báo extern volatile char g_vehicleId[32] trong main.cpp)
// ─────────────────────────────────────────────
extern volatile char g_vehicleId[32];

// ─────────────────────────────────────────────
//  BẢNG ÁNH XẠ UID → TÊN XE (tùy chỉnh)
//  Thêm thẻ thực tế của bạn vào đây
// ─────────────────────────────────────────────
struct VehicleEntry {
    const char* uid;        // UID dạng chuỗi "AA:BB:CC:DD"
    const char* vehicleId;  // Tên/biển số hiển thị
};

// ── SỬA BẢNG NÀY theo thẻ thực tế của bạn ──
// Chạy ở chế độ SCAN_ONLY trước để lấy UID, rồi điền vào đây
static const VehicleEntry VEHICLE_TABLE[] = {
    { "86:7C:63:06", "36A-99999" },
    { "7E:2E:D4:06", "29A-8386" },
};
static const uint8_t VEHICLE_TABLE_SIZE =
    sizeof(VEHICLE_TABLE) / sizeof(VEHICLE_TABLE[0]);

// ─────────────────────────────────────────────
//  CLASS RFID READER
// ─────────────────────────────────────────────
class RfidReader {
private:
    MFRC522 mfrc522;
    char    lastUID[32] = "";          // UID lần đọc cuối
    unsigned long lastReadTime_ms = 0; // Chống đọc lặp

    /**
     * Chuyển UID byte array → chuỗi "AA:BB:CC:DD"
     */
    String uidToString(byte* uid, byte len) {
        String s = "";
        for (byte i = 0; i < len; i++) {
            if (i > 0) s += ":";
            if (uid[i] < 0x10) s += "0";
            s += String(uid[i], HEX);
        }
        s.toUpperCase();
        return s;
    }

    /**
     * Tra bảng ánh xạ UID → vehicleId
     * Nếu không tìm thấy → trả về chính UID làm ID tạm
     */
    const char* lookupVehicle(const String& uid) {
        for (uint8_t i = 0; i < VEHICLE_TABLE_SIZE; i++) {
            if (uid.equals(VEHICLE_TABLE[i].uid)) {
                return VEHICLE_TABLE[i].vehicleId;
            }
        }
        return nullptr;   // Chưa đăng ký
    }

public:
    /**
     * Constructor — truyền chân SS và RST từ config.h
     */
    RfidReader() : mfrc522(PIN_RFID_SS, PIN_RFID_RST) {}

    /**
     * Khởi tạo SPI và RC522
     */
    void begin() {
        SPI.begin();
        mfrc522.PCD_Init();
        mfrc522.PCD_DumpVersionToSerial();   // In phiên bản RC522 ra Serial
        DBGLN("[RFID] RC522 sẵn sàng. Đặt thẻ vào vùng đọc...");

        // Mặc định: chưa biết xe nào
        strncpy((char*)g_vehicleId, "UNKNOWN", sizeof(g_vehicleId) - 1);
    }

    /**
     * Gọi trong loop() mỗi chu kỳ
     * Trả về true nếu vừa đọc được thẻ mới
     */
    bool update() {
        // Chống đọc lặp: bỏ qua nếu đọc trong vòng RFID_DEBOUNCE_MS
        if (millis() - lastReadTime_ms < RFID_DEBOUNCE_MS) return false;

        // Kiểm tra có thẻ mới không
        if (!mfrc522.PICC_IsNewCardPresent()) return false;
        if (!mfrc522.PICC_ReadCardSerial())   return false;

        lastReadTime_ms = millis();

        // ── Đọc UID ──
        String uid = uidToString(
            mfrc522.uid.uidByte,
            mfrc522.uid.size
        );

        // ── Tra bảng xe ──
        const char* mapped = lookupVehicle(uid);
        const char* displayId;

        if (mapped != nullptr) {
            displayId = mapped;
            DBGF("[RFID] Xe đã đăng ký | UID: %s → ID: %s\n",
                 uid.c_str(), displayId);
        } else {
            // Xe lạ → dùng UID trực tiếp làm ID tạm
            displayId = uid.c_str();
            DBGF("[RFID] Xe CHƯA đăng ký | UID: %s (dùng UID làm ID tạm)\n",
                 uid.c_str());
        }

        // ── Ghi vào biến chia sẻ với main.cpp ──
        strncpy((char*)g_vehicleId, displayId, sizeof(g_vehicleId) - 1);
        ((char*)g_vehicleId)[sizeof(g_vehicleId) - 1] = '\0';

        // ── Dừng giao tiếp với thẻ ──
        mfrc522.PICC_HaltA();
        mfrc522.PCD_StopCrypto1();

        return true;
    }

    /**
     * Reset g_vehicleId về UNKNOWN sau khi xe rời khỏi trạm
     * Gọi hàm này sau khi đo tốc độ xong trong main.cpp (tuỳ chọn)
     */
    void resetVehicleId() {
        strncpy((char*)g_vehicleId, "UNKNOWN", sizeof(g_vehicleId) - 1);
        DBGLN("[RFID] Đã reset vehicleId → UNKNOWN");
    }

    /**
     * Chế độ quét để lấy UID thực tế — dùng khi setup lần đầu
     * Gọi hàm này trong setup() thay vì begin() để chỉ in UID ra Serial
     */
    void scanOnlyMode() {
        SPI.begin();
        mfrc522.PCD_Init();
        DBGLN("[RFID] ═══ CHẾ ĐỘ SCAN UID ═══");
        DBGLN("[RFID] Đặt từng thẻ vào đầu đọc để lấy UID.");
        DBGLN("[RFID] Sao chép UID vào VEHICLE_TABLE trong rfid_reader.h");
        DBGLN("[RFID] ════════════════════════");
    }

    bool scanOnlyUpdate() {
        if (!mfrc522.PICC_IsNewCardPresent()) return false;
        if (!mfrc522.PICC_ReadCardSerial())   return false;

        String uid = uidToString(mfrc522.uid.uidByte, mfrc522.uid.size);
        DBGF("[RFID] >>> UID đọc được: %s <<<\n", uid.c_str());
        DBGF("[RFID] Điền vào bảng: { \"%s\", \"TEN_XE\" }\n", uid.c_str());

        mfrc522.PICC_HaltA();
        mfrc522.PCD_StopCrypto1();
        delay(1000);   // Chờ 1 giây trước khi đọc lại
        return true;
    }
};

#endif // RFID_READER_H
