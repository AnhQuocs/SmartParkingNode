🚗 Smart Parking Node

Hệ thống quản lý bãi đỗ xe thông minh sử dụng IoT

## 👥 Thành Viên Nhóm

| Tên | MSSV |
|-----|------|
| Bùi Anh Quốc | 23010328 |
| Hà Mạnh Long | 23010390 |
| Vũ Minh Trung | 23010361 |

---

## 📋 Mục Lục

- [Giới Thiệu](#-giới-thiệu)
- [Tính Năng](#-tính-năng)
- [Kiến Trúc](#-kiến-trúc)
- [Yêu Cầu](#-yêu-cầu)
- [Cài Đặt](#-cài-đặt)
- [Sử Dụng](#-sử-dụng)
- [Cấu Hình](#-cấu-hình)
- [Các Vấn Đề](#-các-vấn-đề)
- [Phân Công](#-phân-công-nhiệm-vụ)

---

## 📌 Giới Thiệu

**Smart Parking Node** là một giải pháp thông minh để quản lý phương tiện ra vào tại các bãi đỗ xe trong trường đại học, doanh nghiệp và tổ chức. Hệ thống kết hợp:

- **Phần cứng IoT**: ESP32 + cảm biến IR + RFID + Servo + DFPlayer Mini
- **Backend**: Firebase Realtime Database + Cloud Firestore
- **Frontend**: Ứng dụng Android native (Jetpack Compose)

Mục tiêu: Tự động xác định hướng di chuyển xe, tính phí gửi xe, quản lý công nợ, giám sát thời gian thực.

---

## ✨ Tính Năng

### 📡 Phía Phần Cứng
- Nhận diện phương tiện qua thẻ RFID RC522
- 2 cảm biến hồng ngoại (IR Sensor) xác nhận hướng di chuyển
- Điều khiển Servo MG90S để mở/đóng Barie tự động
- Cảnh báo âm thanh DFPlayer Mini (nhận dạng thẻ lạ, vượt hạn công nợ)
- Kết nối WiFi để đồng bộ dữ liệu lên Firebase
- Giám sát phần cứng (nhiệt độ, sóng WiFi, trạng thái cảm biến)
- Hoạt động 24/7 với khả năng offline

### 📱 Phía Ứng Dụng Android

**Cho Người Dùng:**
- Xem thông tin cá nhân, mã định danh, công nợ hiện tại
- Sinh mã QR chuyển khoản tự động
- Lịch sử gửi xe chi tiết (thời gian vào/ra, phí tính)
- Lịch sử thanh toán
- Khóa thẻ khẩn cấp khi mất

**Cho Quản Trị Viên:**
- Dashboard thời gian thực (số xe trong bãi, chỗ còn trống)
- Đăng ký thẻ RFID nhanh
- Quản trị thiết bị từ xa (theo dõi nhiệt độ, cảm biến, đổi WiFi)
- Thống kê doanh thu (theo ngày, theo tháng)
- Danh sách tài khoản nợ quá hạn
- Thống kê lưu lượng xe

- Tích hợp Firebase Realtime Database + Cloud Firestore
- Cache offline với Room Database

---

## 🏗️ Kiến Trúc

Hệ thống được thiết kế theo mô hình IoT ba lớp:

```
┌────────────────────────────────────────────────────────┐
│    Firebase Cloud Platform                             │
│  - Realtime DB (dữ liệu nóng - số xe, trạng thái)     │
│  - Cloud Firestore (dữ liệu lạnh - người dùng, logs)  │
└────────────────────┬─────────────────────────────────┘
                     │
        ┌────────────┴─────────────┐
        │                          │
┌───────▼────────────┐  ┌─────────▼──────────────────┐
│  Hardware Layer    │  │  Application Layer        │
│  (ESP32)          │  │  (Android App + Cloud)    │
│ - RFID RC522      │  │ - User Interface          │
│ - IR Sensors      │  │ - Admin Dashboard         │
│ - Servo MG90S     │  │ - Business Logic          │
│ - DFPlayer Mini   │  │ - Firebase Integration    │
│ - Monitoring      │  │ - Room Database (Offline) │
└───────┬──────────┘  └──────────────────────────┬┘
        │                                         │
        │◄────────WiFi + Firebase────────────────►
        │
        ▼
   [IR + RFID + Servo + Audio]
        │
        ▼
   📍 Parking Gate
```

**Quy trình Kiểm Soát Xe:**
1. Người dùng quẹt thẻ RFID
2. ESP32 đọc UID → gửi lên Firebase
3. Android App (Cloud) xác định Check-in/Check-out
4. Kiểm tra công nợ → Cho phép hoặc từ chối
5. ESP32 nhận lệnh → Mở/Đóng Servo
6. Cảm biến IR xác nhận xe đi qua
7. Ghi nhận giao dịch + Cập nhật công nợ
8. Dữ liệu đồng bộ realtime trên ứng dụng

---

## 🔧 Yêu Cầu

### Phần Cứng
- **ESP32 DevKit V1** (NodeMCU-32S): 93.000đ
- **Cảm biến IR** x2: 30.000đ × 2
- **Module RFID RC522** (đọc thẻ): 40.000đ
- **Servo MG90S**: 40.000đ
- **DFPlayer Mini + Loa**: 35.000đ
- **Bộ nguồn ổn định 5V/2A**: 65.000đ
- Các linh kiện phụ trợ (Jumper, khung mô hình, PCB): ~170.000đ

**Tổng chi phí dự kiến: ~549.000đ**

### Phần Mềm
- **PlatformIO** (build firmware ESP32)
- **Android Studio** 2023.3.1+
- **Firebase Project** (Realtime DB + Cloud Firestore)
- **Kotlin** + **Jetpack Compose** (Android)
- **Clean Architecture** + **MVVM** Pattern

---

## 💾 Cài Đặt

### 1. Chuẩn Bị Firebase

**Bước 1:** Tạo Firebase Project tại [firebase.google.com](https://firebase.google.com)

**Bước 2:** Bật Firebase Realtime Database và Cloud Firestore

**Bước 3:** Download `google-services.json` và đặt tại `SOFTWARE/app/`

### 2. Hardware (Firmware)

**Bước 1:** Cài PlatformIO CLI
```bash
pip install platformio
```

**Bước 2:** Mở thư mục HARDWARE
```bash
cd HARDWARE
```

**Bước 3:** Chỉnh sửa cấu hình `include/config.h`
```cpp
#define WIFI_SSID           "YourNetwork"
#define WIFI_PASSWORD       "YourPassword"
#define FIREBASE_HOST       "your-project.firebaseio.com"
#define FIREBASE_AUTH       "your_token"
#define DEBT_LIMIT_VND      100000    // Hạn công nợ tối đa
#define GRACE_PERIOD_MIN    15        // Thời gian miễn phí
```

**Bước 4:** Đấu nối linh kiện theo sơ đồ GPIO [xem phần Cấu Hình]

**Bước 5:** Build & Upload
```bash
pio run -e esp32dev -t upload
```

**Bước 6:** Kiểm tra Serial Monitor
```
[Firebase] Connected
[RFID] Ready
[IR Sensor] Calibrated
```

### 3. Android App

**Bước 1:** Mở thư mục SOFTWARE trong Android Studio
```bash
cd SOFTWARE
```

**Bước 2:** Build & Run
```bash
./gradlew build
./gradlew installDebug
```

Hoặc dùng Android Studio: **Run > Run 'app'**

**Bước 3:** Đăng nhập bằng tài khoản Student (MSSV) hoặc Admin

---

## 🚀 Sử Dụng

### 1. Khởi Động Hệ Thống
- Cấp nguồn cho ESP32
- Chờ kết nối WiFi + Firebase (10-15 giây)
- Kiểm tra Serial Monitor: `[Firebase] Ready`
- Kiểm tra app: Hiển thị "Connected"

### 2. Test Hệ Thống Người Dùng (User)

**Check-in (Xe vào bãi):**
- Quẹt thẻ RFID
- Nếu công nợ < 100.000 VNĐ → Servo mở Barie
- Xe đi qua 2 cảm biến IR
- Hệ thống ghi nhận giờ vào
- App cập nhật tự động

**Check-out (Xe ra bãi):**
- Quẹt thẻ RFID lần 2
- Hệ thống tính thời gian gửi xe
- Tính phí (miễn phí nếu < 15 phút)
- Cộng vào công nợ
- Servo mở cổng ra
- App cập nhật công nợ mới

### 3. Test Hệ Thống Quản Trị Viên (Admin)

- Mở ứng dụng với tài khoản Admin
- Xem Dashboard: Số xe hiện tại, chỗ còn trống
- Xem thống kê doanh thu ngày/tháng
- Đăng ký thẻ RFID lạ
- Quản trị thiết bị từ xa (xem nhiệt độ, cảm biến, đổi WiFi)

### 4. Thanh Toán Công Nợ
- Mở app → Chọn "Thanh Toán"
- Ứng dụng tự sinh mã QR chuyển khoản
- Quét QR trong app ngân hàng → Thanh toán
- Sau 5-10 giây, ứng dụng cập nhật công nợ

---

## ⚙️ Cấu Hình

### GPIO Pins (Trong config.h)
| Linh Kiện | GPIO | Chú Thích |
|-----------|------|----------|
| IR Sensor 1 (vào) | GPIO 35 | Cần pull-up ngoài 10kΩ |
| IR Sensor 2 (ra) | GPIO 33 | |
| RC522 SCK | GPIO 18 | SPI |
| RC522 MOSI | GPIO 23 | SPI |
| RC522 MISO | GPIO 19 | SPI |
| RC522 SDA/CS | GPIO 5 | |
| RC522 RST | GPIO 4 | |
| Servo Signal | GPIO 13 | PWM (180°) |
| DFPlayer RX | GPIO 16 | UART |
| DFPlayer TX | GPIO 17 | UART |

### Cấu Hình Tính Phí
```cpp
#define GRACE_PERIOD_MIN        15      // Thời gian miễn phí (phút)
#define BASE_RATE_VND           5000    // Phí cơ bản (VNĐ)
#define OVERNIGHT_SURCHARGE     10000   // Phụ thu qua đêm (VNĐ)
#define OVERNIGHT_START_HOUR    22      // Thời gian bắt đầu qua đêm
#define DEBT_LIMIT_VND          100000  // Hạn công nợ tối đa (VNĐ)
```

### Bảng Giá Gửi Xe
| Thời Gian | Mức Phí |
|-----------|--------|
| Dưới 15 phút | Miễn phí |
| 15 phút - 22h | 5.000 VNĐ |
| Qua đêm (22h-6h) | 5.000 + 10.000 VNĐ |

### Đăng Ký Thẻ RFID Mới
- Quẹt thẻ lạ tại cổng
- App hiển thị "Thẻ chưa đăng ký"
- Admin chọn sinh viên/nhân viên tương ứng
- Thẻ được kích hoạt ngay lập tức

---

## 📁 Cấu Trúc Dự Án

```
SmartTrafficRadar/
├── README.md
├── HARDWARE/
│   ├── platformio.ini
│   ├── include/
│   │   ├── config.h              (Cấu hình chính - GPIO, WiFi, Firebase)
│   │   ├── firebase_manager.h    (Kết nối Firebase)
│   │   ├── rfid_reader.h         (Đọc UID từ RC522)
│   │   ├── ir_sensor.h           (Xác nhận xe đi qua)
│   │   ├── servo_controller.h    (Điều khiển Barie)
│   │   ├── dfplayer_audio.h      (Phát âm thanh cảnh báo)
│   │   └── system_monitor.h      (Giám sát phần cứng)
│   └── src/
│       └── main.cpp              (Chương trình chính)
│
└── SOFTWARE/
    ├── build.gradle.kts
    ├── settings.gradle.kts
    ├── gradle.properties
    ├── local.properties
    ├── google-services.json
    └── app/
        ├── build.gradle.kts
        ├── proguard-rules.pro
        └── src/
            ├── main/
            │   ├── java/
            │   │   └── com/smartparkingnode/
            │   │       ├── ui/                (UI Screens - User & Admin)
            │   │       ├── viewmodel/         (MVVM ViewModel)
            │   │       ├── repository/        (Firebase Repository)
            │   │       ├── model/             (Data Models)
            │   │       ├── util/              (Utilities)
            │   │       └── MainActivity.kt
            │   └── res/                       (Resources)
            ├── test/                          (Unit Tests)
            └── androidTest/                   (Integration Tests)
```

---

## 🐛 Các Vấn Đề & Giải Pháp

| Vấn Đề | Nguyên Nhân | Giải Pháp |
|--------|-----------|----------|
| GPIO 35 không phát hiện | Không pull-up nội | Gắn 10kΩ ngoài lên 3.3V |
| RFID không đọc | Mâu thuẫn GPIO hoặc khoảng cách | Sử dụng SPI default (18,19,23), điều chỉnh khoảng cách |
| Servo rung | Nguồn điện không đủ | Cấp 5V riêng cho servo, tránh dùng USB |
| Cảm biến IR không ổn định | Nhiều ánh sáng | Điều chỉnh độ nhạy POT trên module IR |
| DFPlayer không phát âm | Kết nối sai, loa bị tắt | Kiểm tra kết nối UART (GPIO 16, 17), kiểm tra âm lượng |
| Firebase timeout | Không kết nối WiFi | Kiểm tra SSID, password, token, tín hiệu WiFi |
| App crash offline | Không cache dữ liệu | Dùng Room Database lưu dữ liệu cục bộ |
| Xe vào rồi bị đóng lại | Timeout revert kích hoạt | Đảm bảo xe đi qua cảm biến IR trong 10 giây |
| Tính phí sai | Lỗi tính toán thời gian | Đồng bộ thời gian ESP32 với Firebase |
| Thẻ lạ không nhận dạng | Thẻ chưa đăng ký | Admin phải đăng ký thẻ trên ứng dụng trước |
| Không thể đổi WiFi từ xa | Mã QR lỗi | Tạo lại mã QR, quét lại từ ứng dụng Android |

---

## 📈 Mở Rộng Tương Lai

- **Camera nhận dạng biển số**: Tự động ghi biển số vào database
- **Hệ thống thanh toán tự động**: Tích hợp thêm e-wallet (Momo, ZaloPay)
- **Dashboard web quản lý**: Quản lý bãi từ máy tính
- **Nhiều node**: Mở rộng thêm cổng ra vào khác
- **OTA firmware update**: Nâng cấp ESP32 không cần đi cáp USB
- **Machine learning**: Dự đoán tình trạng bãi, phân tích hành vi
- **SMS/Email thông báo**: Gửi công nợ qua tin nhắn
- **Kết hợp với hệ thống ghi hình**: Lưu trữ video an ninh

---

## 👨‍💼 Phân Công Nhiệm Vụ

### Vũ Minh Trung - Firmware Engineer
- Lập trình ESP32 kết nối WiFi + Firebase Realtime Database
- Xây dựng cơ chế đọc UID từ RFID RC522
- Xử lý tín hiệu cảm biến hồng ngoại IR (xác nhận xe di chuyển)
- Điều khiển Servo MG90S (mở/đóng Barie)
- Điều khiển DFPlayer Mini (phát âm thanh cảnh báo)
- Triển khai cơ chế Timeout Revert (nếu quẹt thẻ nhưng không đi qua)
- Giám sát phần cứng (nhiệt độ, heap, sóng WiFi)
- Nhận lệnh từ Cloud thực thi

### Hà Mạnh Long - Hardware Engineer
- Thiết kế sơ đồ nguyên lý + sơ đồ đấu nối
- Lắp ráp mô hình bãi đỗ xe + Barie
- Tích hợp RFID, Servo, DFPlayer, cảm biến IR với ESP32
- Quản lý nguồn điện (tránh sụt áp)
- Kiểm tra độ ổn định hệ thống 24/7
- Triển khai Telemetry (gửi thông số lên Firebase)
- Quản lý danh sách WiFi khả dụng

### Bùi Anh Quốc - Cloud & Mobile Engineer
- Thiết kế Hybrid Database (Realtime DB + Cloud Firestore)
- Xây dựng các collections: users, authorized_cards, vehicles_inside, access_logs, payment_transactions, pricing_config
- Thiết lập Firebase Security Rules
- Xây dựng thuật toán Check-in/Check-out
- Tính toán phí + công nợ
- Xây dựng ứng dụng Android (Kotlin + Jetpack Compose)
- Clean Architecture + MVVM Pattern
- Giao diện User (xem nợ, thanh toán, lịch sử)
- Giao diện Admin (dashboard, quản trị thiết bị, thống kê)
- Tích hợp Room Database (cache ngoại tuyến)
- Sinh mã QR thanh toán

---

## 📄 Thông Tin Dự Án

- **Phiên bản**: v1.0
- **Ngôn ngữ**: Tiếng Việt
- **Giấy phép**: MIT
- **Tổng chi phí phần cứng**: ~549.000 VNĐ

---

## 🔗 Tài Liệu Tham Khảo

- [ESP32 Documentation](https://docs.espressif.com/projects/esp-idf/en/latest/esp32/)
- [Firebase Realtime DB](https://firebase.google.com/docs/database)
- [Cloud Firestore](https://firebase.google.com/docs/firestore)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [MFRC522 Module](https://www.nxp.com/docs/en/data-sheet/MFRC522.pdf)
- [RC522 Library for Arduino](https://github.com/miguelbalboa/rfid)
- [Firebase Android SDK](https://firebase.google.com/docs/android/setup)

---

**Happy Coding! 🚀**
