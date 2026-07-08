# Hướng dẫn chạy dự án LaptopStore trên máy khác

Dự án này bao gồm 3 phần chính: **Database** (MySQL), **Backend** (Spring Boot - Java), và **App** (Android Studio). Dưới đây là các bước chi tiết để cấu hình và chạy dự án trên một máy tính mới.

## 1. Yêu cầu hệ thống (Prerequisites)
- **Java Development Kit (JDK):** Phiên bản **21** trở lên (dành cho Backend Spring Boot).
- **Cơ sở dữ liệu:** **MySQL Server** (phiên bản 8.0+). Nên cài thêm MySQL Workbench hoặc phpMyAdmin để dễ quản lý.
- **IDE cho Backend:** **IntelliJ IDEA** (khuyến nghị) hoặc Eclipse.
- **IDE cho App:** **Android Studio** (bản mới nhất hỗ trợ SDK 36).

---

## 2. Bước 1: Cài đặt Database (MySQL)
1. Mở MySQL Workbench (hoặc công cụ quản lý MySQL của bạn).
2. Tạo kết nối tới `localhost` cổng `3306` với user là `root` và mật khẩu là `123456` (Đây là thông tin mặc định đang cài đặt trong Backend).
   - *Lưu ý: Nếu MySQL của máy bạn dùng tài khoản/mật khẩu khác, hãy nhớ đổi lại ở bước Backend bên dưới.*
3. Mở file script `Database/database.sql` có trong thư mục của source code.
4. Chạy (Execute) toàn bộ script này. Nó sẽ tự động tạo database tên là `LaptopStoreDB` và chèn sẵn các dữ liệu cần thiết (bao gồm tài khoản người dùng, sản phẩm mẫu...).

---

## 3. Bước 2: Chạy Backend (Spring Boot)
1. Mở **IntelliJ IDEA**. Chọn **Open** và trỏ tới thư mục `Backend` (ví dụ: `D:\LaptopShop\Backend`).
2. Chờ IDE tải các dependency thông qua Maven (sẽ có thanh tiến trình báo `Syncing...` góc dưới cùng bên phải).
3. (Tuỳ chọn) Nếu tài khoản MySQL của máy bạn khác với mặc định, hãy mở tệp cấu hình tại đường dẫn:
   `src/main/resources/application.properties`
   Sửa lại dòng sau cho phù hợp:
   ```properties
   spring.datasource.username=TÊN_USER_CỦA_BẠN
   spring.datasource.password=MẬT_KHẨU_CỦA_BẠN
   ```
4. Tìm file `LaptopStoreApplication.java` (nằm trong `src/main/java/com/laptopstore`). Chuột phải vào file này và chọn **Run 'LaptopStoreApplication.main()'**.
5. Đợi console báo `Started LaptopStoreApplication in ... seconds`. Lúc này Backend đang chạy tại địa chỉ `http://localhost:8080`.

---

## 4. Bước 3: Chạy App (Android)
1. Mở **Android Studio**. Chọn **Open** và trỏ tới thư mục gốc `LaptopShop` (hoặc mở cụ thể thư mục chứa các file build.gradle của Android).
2. Chờ Android Studio `Gradle Sync` hoàn tất để tải các thư viện Android.
3. **Cấu hình IP máy chủ (Quan trọng):**
   - App Android trên máy ảo (Emulator) hoặc điện thoại thật không thể hiểu `localhost` là Backend của máy tính bạn.
   - Hãy tìm file cấu hình API (thường là `ApiClient.java` hoặc tệp chứa biến `BASE_URL`).
   - Nếu chạy trên **Máy ảo (Emulator) của Android Studio**: Sửa đường dẫn API thành `http://10.0.2.2:8080/api/` (10.0.2.2 là localhost của máy tính đối với máy ảo Android).
   - Nếu chạy trên **Điện thoại thật (cắm cáp) hoặc máy ảo Genymotion**: Bạn cần tìm địa chỉ IP LAN của máy tính (Mở CMD gõ `ipconfig` -> lấy dòng `IPv4 Address`, ví dụ: `192.168.1.15`). Đổi BASE_URL thành `http://192.168.1.15:8080/api/`.
4. Nhấn nút **Run 'app'** (Biểu tượng tam giác xanh lá) trên Android Studio để cài đặt và chạy ứng dụng lên máy ảo / điện thoại.

---

## Tài khoản dùng thử (Nếu có)
Các tài khoản mẫu đã được tạo sẵn khi bạn chạy script SQL. Ví dụ:
- **Admin**: (Xem trong dữ liệu bảng `users` ở DB)
- **User**: (Xem trong dữ liệu bảng `users` ở DB)
*(Mật khẩu mặc định trong sql cho các user được băm Bcrypt, thông thường là `password` hoặc `123456` tuỳ cấu hình trong script database).*
