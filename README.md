# 🚗 Smart Parking Node

Hệ thống quản lý bãi đỗ xe thông minh dùng **ESP32 + Firebase + Spring Boot + Android**.

## 👥 Thành viên nhóm

| Tên | MSSV |
|-----|------|
| Bùi Anh Quốc | 23010328 |
| Hà Mạnh Long | 23010390 |
| Vũ Minh Trung | 23010361 |

## 📌 Tổng quan

Hệ thống tự động kiểm soát xe ra/vào bãi bằng thẻ RFID, cảm biến hồng ngoại, servo barie và âm thanh cảnh báo. Dữ liệu được đồng bộ qua Firebase, backend Spring Boot xử lý nghiệp vụ trung tâm, còn ứng dụng Android dùng để theo dõi, quản trị và thanh toán công nợ.

## ✨ Chức năng chính

### 1) Phần cứng (`HARDWARE`)
- Đọc UID thẻ RFID RC522.
- Xác nhận xe đi qua bằng 2 cảm biến IR.
- Điều khiển servo mở/đóng barie.
- Phát âm thanh cảnh báo/thông báo bằng DFPlayer Mini.
- Kết nối Wi‑Fi, đồng bộ Firebase và nhận lệnh cloud.
- Hỗ trợ chế độ đăng ký thẻ, telemetry thiết bị và tự khôi phục khi mất kết nối.

### 2) Backend Spring Boot (`PAYMENT_BACKEND`)
- Kiểm tra thẻ RFID: thẻ lạ, thẻ khóa, công nợ vượt ngưỡng.
- Xác định luồng vào/ra và trả lệnh mở cổng cho hardware.
- Ghi nhận sự kiện xe vào/ra, cập nhật lịch sử và analytics realtime.
- Quản lý công nợ, lịch sử thanh toán và thông báo FCM.
- Tạo link thanh toán MoMo và xử lý callback/IPN.
- Lưu FCM token, hỗ trợ tác vụ nhắc xe gửi lâu/qua đêm.

### 3) Ứng dụng Android (`SOFTWARE`)
- Đăng nhập, hồ sơ người dùng và quản trị viên.
- Dashboard realtime: số xe trong bãi, trạng thái thiết bị, thống kê cơ bản.
- Xem lịch sử gửi xe, lịch sử thanh toán và công nợ.
- Sinh mã QR/thực hiện thanh toán công nợ.
- Quản lý thẻ RFID, xe, thông báo và các yêu cầu đăng ký.
- Đồng bộ Firebase Realtime Database + Firestore, lưu trạng thái cục bộ bằng DataStore.

## 🏗️ Kiến trúc ngắn gọn

```text
RFID / IR / Servo / DFPlayer  →  ESP32  →  Firebase  →  Spring Boot
                                      ↘               ↘
                                       Android App     MoMo / FCM
```

Luồng hoạt động chính:
1. Người dùng quẹt thẻ RFID.
2. ESP32 gửi UID lên backend để kiểm tra.
3. Backend trả quyền **mở cổng / từ chối**.
4. ESP32 điều khiển barie, IR xác nhận xe qua thực tế.
5. Backend ghi lịch sử, tính phí, cập nhật công nợ và gửi thông báo.

## 🔧 Công nghệ sử dụng

- **Hardware**: ESP32, RC522, IR sensor, Servo MG90S, DFPlayer Mini.
- **Backend**: Java, Spring Boot, Firebase Admin SDK, MoMo API, FCM.
- **Mobile**: Kotlin, Jetpack Compose, Firebase Firestore/Realtime Database, DataStore.

## 📁 Cấu trúc chính

```text
SmartTrafficRadar/
├── HARDWARE/          # Firmware ESP32
├── PAYMENT_BACKEND/   # Spring Boot backend + MoMo + Firebase
└── SOFTWARE/          # Ứng dụng Android
```

## ⚙️ Yêu cầu chạy

- **HARDWARE**: PlatformIO + ESP32 + cấu hình Wi‑Fi/Firebase trong `include/config.h`.
- **PAYMENT_BACKEND**: JDK + Gradle, cấu hình Firebase service account và MoMo trong `application.properties`.
- **SOFTWARE**: Android Studio + Firebase project + `google-services.json`.

## 🚀 Khởi chạy nhanh

### Firmware
```bash
cd HARDWARE
pio run -e esp32dev -t upload
```

### Backend
```bash
cd PAYMENT_BACKEND
./gradlew bootRun
```

### Android app
```bash
cd SOFTWARE
./gradlew build
```

## 📚 Ghi chú

- Hệ thống ưu tiên hoạt động realtime qua Firebase.
- Một số ngưỡng như công nợ tối đa, thời gian nhắc nhở và phí gửi xe được cấu hình trong code/backend.
- Khi viết báo cáo, có thể mô tả hệ thống theo 3 lớp: **Hardware – Backend – Software**.

## 🔗 Tài liệu tham khảo

- [ESP32 Documentation](https://docs.espressif.com/projects/esp-idf/en/latest/esp32/)
- [Firebase Realtime Database](https://firebase.google.com/docs/database)
- [Cloud Firestore](https://firebase.google.com/docs/firestore)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [RC522 Library](https://github.com/miguelbalboa/rfid)

---

