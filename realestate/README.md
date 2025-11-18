

# Real Estate News Management 

## Technology Stack

| Thành phần | Công cụ |
|------------|---------|
| Backend    | Java 17+, Spring Boot 3.x |
| Cơ sở dữ liệu | MySQL 8+ |
| ORM & Migration | Spring Data JPA, Flyway |
| Bảo mật    | Spring Security, JWT, OTP Phone |
| Email & Notifications | Spring Boot Starter Mail |
| Bộ nhớ đệm | Spring Cache (Simple) |
| Frontend   | Bootstrap 5, Chart.js, Google Maps API |
| IDE        | IntelliJ IDEA / VS Code / Eclipse |
| Build      | Maven |
| Test API   | Postman |
| Quản lý CSDL | MySQL Workbench |

## Chức năng theo mô-đun & vai trò

### 1. Khách truy cập (Guest)
- Trang chủ hiển thị tin mới, tin nổi bật, dự án nổi bật và bài viết tin tức.
- Bộ lọc nâng cao: mục đích (bán/thuê), loại bất động sản, vị trí (tỉnh → quận → phường), giá, diện tích, số phòng, hướng, nội thất, pháp lý.
- Trang chi tiết tin đăng: thư viện ảnh/video, mô tả, tiện ích, bản đồ Google Maps, thông tin liên hệ.
- Danh sách và chi tiết tin tức, danh sách và chi tiết dự án bất động sản.
- Đăng ký và đăng nhập để chuyển sang vai trò người dùng.

### 2. Người dùng đăng nhập (User)
- Quản lý hồ sơ: xem/sửa thông tin, đổi mật khẩu, cập nhật avatar.
- Xác thực OTP số điện thoại (bắt buộc trước khi đăng tin).
- Đăng tin: nhập thông tin bất động sản, tải lên 3–24 ảnh và video, chọn gói tin (miễn phí/nổi bật), gửi duyệt.
- Quản lý tin của tôi: sửa, ẩn/hiện, xóa, gia hạn, xem thống kê lượt xem và yêu thích.
- Quản lý yêu thích: lưu/huỷ lưu tin, xem danh sách yêu thích.
- Dashboard cá nhân: tổng quan tin theo trạng thái, tổng lượt xem/yêu thích, top tin 7/30 ngày, biểu đồ lượt xem theo ngày.
- Tìm kiếm gần tôi theo bán kính và xem vị trí trên Google Maps.

### 3. Quản trị viên (Admin)
- Kiểm duyệt tin chờ duyệt: duyệt, từ chối kèm lý do, khóa/xóa tin vi phạm.
- Quản lý người dùng: danh sách, khóa/mở khóa tài khoản, nâng/hạ vai trò.
- Quản lý nội dung: tin tức, dự án, banner/quảng cáo.
- Thống kê hệ thống: người dùng, tin, lượt xem, báo cáo theo thời gian.
- Nhật ký hệ thống (audit log), cấu hình SMTP, lưu trữ media và các tham số vận hành.

## Thiết kế cơ sở dữ liệu

### 1. Người dùng & bảo mật
- **users**(id, email UNIQUE, password_hash, full_name, phone UNIQUE NULL, avatar_url, is_active, created_at)
- **roles**(id, name) với dữ liệu mẫu: ROLE_USER, ROLE_ADMIN
- **user_roles**(user_id, role_id) với khóa chính (user_id, role_id)
- **otp_verifications**(id, user_id, phone, otp_code, expires_at, verified_at)
- Chỉ mục: users(email), users(phone)

### 2. Danh mục & địa lý
- **property_types**(id, code, name)
- **provinces**(id, name)
- **districts**(id, province_id, name)
- **wards**(id, district_id, name)
- **orientations**(id, name)
- **furnishing_levels**(id, name)
- **amenities**(id, name)

### 3. Tin đăng
- **posts**(id, owner_user_id, title, description, purpose ENUM('sell','rent'), property_type_id, province_id, district_id, ward_id, address, price DECIMAL, area DECIMAL, bedrooms INT, bathrooms INT, orientation_id NULL, furnishing_level_id NULL, legal_status VARCHAR(64) NULL, latitude DECIMAL(10,7) NULL, longitude DECIMAL(10,7) NULL, status ENUM('draft','pending','approved','rejected','hidden','expired'), views_total INT DEFAULT 0, expires_at DATETIME NULL, created_at, updated_at)
- Chỉ mục: purpose, property_type_id, province_id, district_id, ward_id, price, area, status, created_at, latitude/longitude (hoặc POINT SRID 4326 + SPATIAL INDEX)
- Full-text index: FULLTEXT(title, description)
- **post_media**(id, post_id, url, media_type ENUM('image','video'), sort_order)
- **post_amenities**(post_id, amenity_id) với khóa chính (post_id, amenity_id)

### 4. Tương tác & thống kê
- **favorites**(user_id, post_id, created_at) với khóa chính (user_id, post_id) và chỉ mục phụ post_id
- **post_views**(id, post_id, viewer_user_id NULL, viewer_ip, viewed_at) + UNIQUE(post_id, viewer_user_id, viewer_ip, DATE(viewed_at))
- Tùy chọn **reports**(id, post_id, reporter_user_id, reason, created_at)

### 5. Nội dung khác
- **news**(id, title, slug UNIQUE, summary, content, cover_url, published_at, is_published)
- **projects**(id, name, investor, province_id, district_id, ward_id, address, price_from, price_to, status ENUM('planning','open','sold_out'), lat, lng, description, cover_url, created_at)
- **banners**(id, position, image_url, target_url, is_active, sort_order)

### 6. Thanh toán (gói nổi bật)
- **plans**(id, code, name, days, price)
- **orders**(id, user_id, plan_id, amount, status ENUM('created','paid','failed'), paid_at, created_at)
- **post_plan**(post_id, plan_id, start_at, end_at)

Tất cả khóa ngoại sử dụng ON UPDATE CASCADE và ON DELETE RESTRICT. Các chỉ mục kết hợp như (status, purpose, property_type_id, province_id, price, area, created_at) giúp tăng tốc truy vấn danh sách.

## Kiến trúc & triển khai

### 1. Kiến trúc ứng dụng
- Mô hình lớp: Controller → Service → Repository (Spring Data JPA) → MySQL.
- Spring Security + JWT (Bearer) cho REST; có thể bổ sung session form-login nếu dùng Thymeleaf.
- Vai trò: ROLE_USER, ROLE_ADMIN với phân quyền hasRole('ADMIN') cho /api/admin/**.
- Bean Validation `@Valid` trên DTO, mapping bằng MapStruct hoặc mapper thủ công.
- Cấu trúc thư mục gợi ý:

```
src/
 ├─ config/          # SecurityConfig, WebMvcConfig, S3Config…
 ├─ auth/            # JwtFilter, AuthController, DTO
 ├─ users/           # UserController, UserService, UserRepository…
 ├─ posts/           # PostController, MediaController, PostService…
 ├─ catalog/         # PropertyType, Amenity, Orientation…
 ├─ news/            # News module
 ├─ projects/        # Project module
 ├─ stats/           # Dashboard & reports
 ├─ common/          # DTO chung, exception handler, utils, mapper
 └─ interceptors/    # PostViewInterceptor, Audit interceptor
```

### 2. Đăng ký/Đăng nhập/OTP
- `POST /auth/register` lưu users với mật khẩu BCrypt và gán ROLE_USER.
- `POST /auth/login` trả JWT (kèm refresh token nếu cần).
- OTP điện thoại: `POST /auth/phone/otp` gửi mã; `POST /auth/phone/verify` xác thực, cập nhật users.phone và cờ xác thực.

### 3. Quy trình đăng tin
- Người dùng tạo tin draft → nhập chi tiết → tải media → gửi duyệt (status = pending).
- Upload media qua `POST /api/posts/{id}/media` (Multipart) lưu local hoặc S3, tạo thumbnail, kiểm tra số lượng 3–24.
- Nếu mua gói nổi bật: tạo orders, post_plan, thiết lập expires_at.
- Khi hết hạn, scheduler `@Scheduled` chuyển trạng thái expired.

### 4. Kiểm duyệt (Admin)
- Endpoint `GET /api/admin/posts?status=pending` để xem tin chờ.
- `PATCH /api/admin/posts/{id}/approve` hoặc `/reject` (body chứa lý do), cập nhật trạng thái và ghi audit log.
- Tin vi phạm có thể chuyển trạng thái hidden/rejected hoặc xóa.

### 5. Tìm kiếm & lọc
- `GET /api/posts` hỗ trợ tham số: purpose, propertyType, province, district, ward, priceMin/Max, areaMin/Max, bedrooms, bathrooms, orientation, furnishing, legalStatus, sort, page, size.
- Full-text với tham số q (title, description).
- Tìm quanh tôi: truyền lat, lng, radiusKm và sử dụng truy vấn Haversine hoặc GIS.
- Phân trang bằng Spring Data Pageable, cache danh mục bằng Spring Cache.

### 6. Google Maps
- Form đăng tin: Autocomplete địa chỉ + kéo marker để ghi latitude/longitude.
- Trang chi tiết tin và dự án: hiển thị bản đồ, có thể thêm chế độ "map view" với cluster marker.
- Bảo vệ API key bằng referrer/domain và biến môi trường.

### 7. Upload media
- Kiểm tra mime type (Apache Tika), dung lượng; giới hạn định dạng ảnh/video.
- Lưu file vào `/uploads/yyyy/MM/uuid.ext` hoặc dịch vụ S3/MinIO; lưu đường dẫn vào post_media.
- Có thể dùng CDN để tăng tốc tải ảnh.

### 8. Ghi nhận lượt xem & dashboard
- Interceptor ghi vào post_views (giới hạn 1 lần/ngày theo user/IP) và tăng posts.views_total.
- Dashboard cá nhân: tổng hợp số liệu trạng thái tin, tổng view, tổng favorites, top tin 7/30 ngày, biểu đồ views-by-day (Chart.js).
- Dashboard admin: thống kê người dùng, tin, lượt xem, báo cáo theo thời gian.

### 9. Email & thông báo
- Cấu hình SMTP trong application.yml để gửi email đăng ký, quên mật khẩu, duyệt/từ chối tin, nhắc hết hạn.
- Có thể bổ sung WebSocket/STOMP cho thông báo real-time.

### 10. Bảo mật & hiệu năng
- Ma trận quyền: Guest chỉ đọc, User quản lý tin của mình, Admin toàn quyền.
- Bật CSRF khi dùng form, cấu hình CORS cho REST.
- Giới hạn tần suất gửi OTP, log thất bại đăng nhập.
- Dùng chỉ mục tổng hợp cho truy vấn phổ biến (status, created_at, price, area…).
- Dự phòng hình ảnh: CDN, backup định kỳ.

## API chính (tóm tắt)

### Auth
- POST `/auth/register`
- POST `/auth/login`
- POST `/auth/phone/otp`
- POST `/auth/phone/verify`

### Posts (User)
- GET `/api/posts`
- GET `/api/posts/{id}`
- POST `/api/posts`
- PUT `/api/posts/{id}`
- PATCH `/api/posts/{id}/status`
- DELETE `/api/posts/{id}`
- POST `/api/posts/{id}/media`
- DELETE `/api/posts/{id}/media/{mediaId}`

### Favorites (User)
- POST `/api/favorites/{postId}`
- DELETE `/api/favorites/{postId}`
- GET `/api/favorites`

### Admin moderation
- GET `/api/admin/posts?status=pending`
- PATCH `/api/admin/posts/{id}/approve`
- PATCH `/api/admin/posts/{id}/reject`
- PATCH `/api/admin/users/{id}/lock`
- PATCH `/api/admin/users/{id}/unlock`

### Dashboard (User)
- GET `/api/me/dashboard`

### News & Projects
- GET `/api/news`
- GET `/api/news/{id}`
- POST/PUT/DELETE `/api/admin/news`
- GET `/api/projects`
- GET `/api/projects/{id}`
- POST/PUT/DELETE `/api/admin/projects`

## Quy trình triển khai

1. Khởi tạo dự án Spring Boot với các starter: Web, Security, Validation, Data JPA, MySQL, Mail, DevTools.
2. Cấu hình datasource và Flyway/Liquibase để quản lý schema dựa trên thiết kế bảng.
3. Xây dựng module authentication (JWT, register/login, refresh token, OTP điện thoại).
4. Triển khai module danh mục và địa lý (REST read-only, cache dữ liệu tĩnh).
5. Phát triển module posts với DTO, validation, upload media, lọc/search, tính năng gần tôi.
6. Bổ sung favorites, post views, báo cáo dashboard cá nhân.
7. Tạo module news/projects/banners cho quản trị.
8. Hoàn thiện nghiệp vụ kiểm duyệt, quản lý người dùng và audit log cho admin.
9. Tích hợp Google Maps, email, scheduler xử lý tin hết hạn và các job nền.
10. Viết unit/integration test, kiểm tra bảo mật, tối ưu chỉ mục, cấu hình cache/CDN.
11. Thiết lập CI/CD, build Maven, deploy (Docker + Nginx reverse proxy), cấu hình HTTPS, backup MySQL, giám sát.

## Hướng dẫn chạy chương trình

### Yêu cầu hệ thống
- JDK 17 trở lên.
- Maven 3.9+.
- MySQL 8 (đã tạo database ví dụ `realestate`).
- (Tuỳ chọn) MailHog hoặc SMTP server để kiểm tra email OTP/thông báo.

### Cấu hình ứng dụng
1. Sao chép file cấu hình mặc định `src/main/resources/application.yml` và điều chỉnh thông tin kết nối MySQL (`spring.datasource.url`, `username`, `password`) phù hợp với môi trường của bạn. Bạn cũng có thể thiết lập thông qua biến môi trường theo cú pháp Spring Boot (ví dụ `SPRING_DATASOURCE_URL`).
2. Cập nhật `app.jwt.secret` bằng chuỗi bí mật đủ dài và khó đoán.
3. Điều chỉnh cấu hình email (`spring.mail.*`) nếu gửi qua SMTP thực tế.
4. Bảo đảm thư mục lưu trữ file upload (mặc định `uploads/`) tồn tại và tiến trình có quyền ghi.

### Khởi chạy bằng Maven
```bash
mvn clean package
mvn spring-boot:run
```

Ứng dụng mặc định lắng nghe tại `http://localhost:8080`. Flyway sẽ tự động chạy các migration trong `src/main/resources/db/migration` để khởi tạo schema ngay lần đầu tiên kết nối tới cơ sở dữ liệu.

### Chạy kiểm thử
```bash
mvn test
```

Khi phát triển bằng VS Code, cài đặt các extension Java (Extension Pack for Java) và sử dụng `Run`/`Debug` ngay trên lớp `RealEstateApplication` để khởi động ứng dụng.

---

Tổng hợp yêu cầu chức năng, thiết kế dữ liệu, kiến trúc và quy trình xây dựng hệ thống quản lý tin bất động sản dựa trên Spring Boot, Bootstrap và MySQL.
