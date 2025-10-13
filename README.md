# API Documentation

Tài liệu này cung cấp thông tin chi tiết về các endpoint API cho ứng dụng Cookbook.

Base URL: /api
## 1. User API

Endpoint quản lý thông tin người dùng.

Controller: UserController
Base Path: /api/users
### 1.1 Lấy danh sách tất cả người dùng

    Method: GET

    Endpoint: /api/users

    Mô tả: Trả về danh sách tất cả người dùng trong hệ thống.

    Responses:

        200 OK: Trả về một mảng chứa thông tin các người dùng.

        [
            {
                "id": 1,
                "email": "user1@example.com",
                "fullName": "Nguyen Van A",
                "avatar": "url_to_avatar.jpg"
            },
            {
                "id": 2,
                "email": "user2@example.com",
                "fullName": "Tran Thi B",
                "avatar": null
            }
        ]

###  1.2 Lấy thông tin người dùng theo ID

    Method: GET

    Endpoint: /api/users/{id}

    Mô tả: Trả về thông tin chi tiết của một người dùng dựa trên ID.

    Parameters:

        id (path variable, Long): ID của người dùng cần lấy.

    Responses:

        200 OK: Trả về thông tin chi tiết của người dùng.

        {
            "id": 1,
            "email": "user1@example.com",
            "fullName": "Nguyen Van A",
            "avatar": "url_to_avatar.jpg"
        }

        404 Not Found: Không tìm thấy người dùng với ID đã cung cấp.

### 1.3 Tạo người dùng mới

    Method: POST

    Endpoint: /api/users

    Mô tả: Tạo một người dùng mới. (Thường thì chức năng này sẽ nằm trong API đăng ký, nhưng được cung cấp ở đây để quản lý).

    Request Body:

    {
        "email": "newuser@example.com",
        "password": "SecurePassword123",
        "fullName": "Le Van C"
    }

    Responses:

        200 OK: Người dùng được tạo thành công, trả về thông tin người dùng đã tạo.

        {
            "id": 3,
            "email": "newuser@example.com",
            "fullName": "Le Van C",
            "avatar": null
        }

        400 Bad Request: Dữ liệu đầu vào không hợp lệ (ví dụ: email sai định dạng, thiếu trường bắt buộc).

### 1.4 Cập nhật thông tin người dùng

    Method: PUT

    Endpoint: /api/users/{id}

    Mô tả: Cập nhật thông tin profile của người dùng (không bao gồm email/password).

    Parameters:

        id (path variable, Long): ID của người dùng cần cập nhật.

    Request Body:

    {
        "fullName": "Le Van C Updated",
        "avatar": "new_avatar_url.png"
    }

    Responses:

        200 OK: Cập nhật thành công, trả về thông tin người dùng sau khi cập nhật.

        404 Not Found: Không tìm thấy người dùng để cập nhật.

### 1.5 Xóa người dùng

    Method: DELETE

    Endpoint: /api/users/{id}

    Mô tả: Xóa một người dùng khỏi hệ thống.

    Parameters:

        id (path variable, Long): ID của người dùng cần xóa.

    Responses:

        204 No Content: Xóa người dùng thành công.

        404 Not Found: Không tìm thấy người dùng để xóa.

### 1.6 Kiểm tra email đã tồn tại

    Method: GET

    Endpoint: /api/users/exists

    Mô tả: Kiểm tra xem một địa chỉ email đã được đăng ký trong hệ thống hay chưa.

    Query Parameters:

        email (String): Email cần kiểm tra.

    Example: /api/users/exists?email=test@example.com

    Responses:

        200 OK: Trả về true nếu email đã tồn tại, false nếu chưa.

        true

## 2. Authentication API

Endpoint quản lý các quy trình xác thực như đăng ký, đăng nhập.

Controller: AuthController
Base Path: /api/auth
### 2.1 Gửi mã OTP

    Method: POST

    Endpoint: /api/auth/send-otp

    Mô tả: Tạo và gửi một mã OTP (One-Time Password) đến địa chỉ email được cung cấp để xác thực cho việc đăng ký.

    Query Parameters:

        email (String): Email của người dùng muốn đăng ký.

    Example: /api/auth/send-otp?email=register.test@example.com

    Responses:

        200 OK: Gửi OTP thành công.

        OTP đã được gửi đến email

        400 Bad Request: Email không hợp lệ hoặc không thể gửi.

### 2.2 Đăng ký tài khoản mới

    Method: POST

    Endpoint: /api/auth/register

    Mô tả: Hoàn tất quá trình đăng ký bằng cách xác thực OTP và tạo tài khoản người dùng mới.

    Request Body:

    {
        "email": "register.test@example.com",
        "password": "StrongPassword!123",
        "fullName": "New Register User",
        "otp": "123456"
    }

    Responses:

        200 OK: Đăng ký tài khoản thành công.

        Đăng ký thành công

        400 Bad Request:

            OTP không hợp lệ hoặc đã hết hạn.

            Email đã tồn tại.

            Dữ liệu không hợp lệ (password yếu, thiếu trường).

### 2.3 Đăng nhập

    Method: POST

    Endpoint: /api/auth/login

    Mô tả: Xác thực thông tin đăng nhập của người dùng và trả về một JSON Web Token (JWT) nếu thành công.

    Query Parameters:

        username (String): Tên đăng nhập của người dùng (trong trường hợp này là email).

        password (String): Mật khẩu của người dùng.

    Example: /api/auth/login?username=user1@example.com&password=password123

    Responses:

        200 OK: Đăng nhập thành công, trả về JWT.

        eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMUBleGFtcGxlLmNvbSIsImlhdCI6MTY3OTg0NjIwNSwiZXhwIjoxNjc5ODQ5ODA1fQ.random_jwt_string

        401 Unauthorized: Thông tin đăng nhập không hợp lệ (sai email hoặc mật khẩu).