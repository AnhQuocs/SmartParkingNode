# 🚦 SmartTrafficRadar

Hệ thống phát hiện vi phạm tốc độ giao thông thông minh sử dụng IoT

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

---

## 📌 Giới Thiệu

**SmartTrafficRadar** là một giải pháp thông minh để phát hiện và ghi nhận các phương tiện vi phạm tốc độ trên đường. Hệ thống kết hợp:

- **Phần cứng IoT**: ESP32 + cảm biến IR + RFID + Servo
- **Backend**: Firebase Realtime Database
- **Frontend**: Ứng dụng Android native (Jetpack Compose)

Mục tiêu: Tự động ghi nhận vi phạm, gửi dữ liệu cloud, quản lý thống kê thời gian thực.

---

## ✨ Tính Năng

### 📡 Phía Phần Cứng
- Đo tốc độ từ 2 cảm biến hồng ngoại (IR Sensor)
- Nhận dạng phương tiện qua thẻ RFID RC522
- Kích hoạt cảnh báo bằng servo MG90S (chỉ vào vi phạm)
- Kết nối WiFi để đẩy dữ liệu lên Firebase
- Hoạt động 24/7 với khả năng offline

### 📱 Phía Ứng Dụng Android
- Hiển thị phương tiện vi phạm realtime
- Thống kê chi tiết (tổng xe, số lần vi phạm, tốc độ trung bình)
- Lịch sử sự kiện chi tiết
- Tích hợp Firebase Realtime Database
- Cache offline với DataStore

---

## 🏗️ Kiến Trúc

```
┌──────────────────┐
│    Firebase      │ ← Cloud database
│  Realtime DB     │
└────────┬─────────┘
         │
    ┌────┴────┐
    │          │
┌───▼─────┐  ┌▼────────────┐
│ Hardware │  │  Android    │
│ (ESP32)  │  │  App        │
└─────────┘  └─────────────┘
    ▲
    │
  [IR + RFID + Servo]
```

**Quy trình:**
1. Xe vào → Cảm biến IR phát hiện
2. RFID đọc thẻ → Lấy ID xe
3. Tính tốc độ = khoảng cách / thời gian
4. Nếu vượt tốc độ → Kích hoạt servo (laser chỉ)
5. Đẩy dữ liệu lên Firebase
6. Android app hiển thị realtime

---

## 🔧 Yêu Cầu

### Phần Cứng
- **ESP32 DevKit v1** (NodeMCU-32S)
- **Cảm biến IR** x2
- **Module RFID RC522** (đọc thẻ)
- **Servo MG90S** (xoay 180°)
- **Nguồn điện**: 5V/2A

### Phần Mềm
- **PlatformIO** (build firmware ESP32)
- **Android Studio** 2023.3.1+
- **Firebase Project** (Realtime DB)

---

## 💾 Cài Đặt

### 1. Hardware (Firmware)

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
#define WIFI_SSID       "YourNetwork"
#define WIFI_PASSWORD   "YourPassword"
#define FIREBASE_HOST   "your-project.firebaseio.com"
#define FIREBASE_AUTH   "your_token"
```

**Bước 4:** Build & Upload
```bash
pio run -e esp32dev -t upload
```

### 2. Android App

**Bước 1:** Tạo Firebase Project tại [firebase.google.com](https://firebase.google.com)

**Bước 2:** Download `google-services.json` và đặt tại `SOFTWARE/app/`

**Bước 3:** Mở thư mục SOFTWARE trong Android Studio
```bash
cd SOFTWARE
```

**Bước 4:** Build & Run
```bash
./gradlew build
./gradlew installDebug
```

Hoặc dùng Android Studio: **Run > Run 'app'**

---

## 🚀 Sử Dụng

### 1. Khởi Động Hệ Thống
- Cấp nguồn cho ESP32
- Chờ kết nối WiFi (5-10 giây)
- Kiểm tra Serial Monitor: `[Firebase] Ready`

### 2. Test Hệ Thống
- Để xe chạy qua 2 cảm biến IR
- Hệ thống tính tốc độ tự động
- Nếu vượt tốc độ → Servo xoay
- Dữ liệu được lưu trên Firebase

### 3. Xem Trên App
- Mở ứng dụng SmartTrafficRadar
- Xem phương tiện mới nhất
- Xem thống kê ngày

---

## ⚙️ Cấu Hình

### GPIO Pins (Trong config.h)
| Linh Kiện | GPIO | Chú Thích |
|-----------|------|----------|
| IR Sensor 1 | GPIO 35 | Cần pull-up ngoài 10kΩ |
| IR Sensor 2 | GPIO 33 | |
| RC522 SCK | GPIO 18 | SPI |
| RC522 MOSI | GPIO 23 | SPI |
| RC522 MISO | GPIO 19 | SPI |
| RC522 SDA/CS | GPIO 5 | |
| RC522 RST | GPIO 4 | |
| Servo Signal | GPIO 13 | PWM |

### Cấu Hình Tốc Độ
```cpp
#define VMAX_DEFAULT_KMH    60      // Tốc độ tối đa (km/h)
#define DISTANCE_METERS     1.5     // Khoảng cách 2 cảm biến
```

### Thêm Xe RFID
Chỉnh sửa `include/rfid_reader.h`:
```cpp
VehicleEntry vehicleDB[] = {
    {"AA:BB:CC:DD", "BKS10001"},
    {"11:22:33:44", "BKS10002"},
};
```

---

## 📁 Cấu Trúc Dự Án

```
SmartTrafficRadar/
├── README.md
├── HARDWARE/
│   ├── platformio.ini
│   ├── include/
│   │   ├── config.h              (Cấu hình chính)
│   │   ├── firebase_manager.h    (Firebase)
│   │   ├── rfid_reader.h         (RFID)
│   │   ├── speed_sensor.h        (Đo tốc độ)
│   │   └── servo_controller.h    (Servo)
│   └── src/
│       └── main.cpp
│
└── SOFTWARE/
    ├── build.gradle.kts
    ├── settings.gradle.kts
    └── app/
        ├── build.gradle.kts
        ├── google-services.json
        └── src/
            └── main/
```

---

## 🐛 Các Vấn Đề & Giải Pháp

| Vấn Đề | Nguyên Nhân | Giải Pháp |
|--------|-----------|----------|
| GPIO 35 không phát hiện | Không pull-up nội | Gắn 10kΩ ngoài lên 3.3V |
| RFID không đọc | Mâu thuẫn GPIO | Dùng SPI default (18,19,23) |
| Servo rung | Nguồn điện không đủ | Cấp 5V riêng cho servo |
| Firebase timeout | Không kết nối WiFi | Kiểm tra SSID, password, token |
| App crash offline | Không có cache | Dùng DataStore lưu dữ liệu |

---

## 📈 Mở Rộng

- Thêm laser + buzzer cảnh báo
- Thêm camera để nhận dạng biển số
- Lưu trữ dài hạn trên Cloud Firestore
- Dashboard web
- Nhiều node (multi-node sync)
- OTA firmware update

---

## 📄 Thông Tin

- **Phiên bản**: v1.0
- **Ngôn ngữ**: Tiếng Việt
- **Giấy phép**: MIT

---

## 🔗 Tài Liệu Tham Khảo

- [ESP32 Documentation](https://docs.espressif.com/projects/esp-idf/en/latest/esp32/)
- [Firebase Realtime DB](https://firebase.google.com/docs/database)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [MFRC522 Module](https://www.nxp.com/docs/en/data-sheet/MFRC522.pdf)

---

**Happy Coding! 🚀**
