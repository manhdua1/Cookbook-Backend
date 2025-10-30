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

    Mô tả: Cập nhật thông tin profile của người dùng (fullName, avatarUrl, bio, hometown). KHÔNG thể thay đổi email/password.

    Parameters:

        id (path variable, Long): ID của người dùng cần cập nhật.

    Request Body:

    {
        "fullName": "Nguyễn Văn A Updated",
        "avatarUrl": "https://example.com/new_avatar.jpg",
        "bio": "Tôi yêu thích nấu ăn và khám phá món ăn mới",
        "hometown": "Hà Nội"
    }

    Validation Rules:

        fullName: Bắt buộc, tối đa 100 ký tự
        avatarUrl: Tùy chọn, tối đa 255 ký tự
        bio: Tùy chọn, tối đa 500 ký tự
        hometown: Tùy chọn, tối đa 100 ký tự

    Responses:

        200 OK: Cập nhật thành công, trả về thông tin người dùng sau khi cập nhật.

        {
            "id": 1,
            "email": "user@example.com",
            "fullName": "Nguyễn Văn A Updated",
            "avatarUrl": "https://example.com/new_avatar.jpg",
            "bio": "Tôi yêu thích nấu ăn và khám phá món ăn mới",
            "hometown": "Hà Nội"
        }

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

### 1.7 Lấy thông tin người dùng hiện tại

    Method: GET

    Endpoint: /api/users/me

    Mô tả: Lấy thông tin profile của người dùng hiện tại dựa trên JWT token. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Responses:

        200 OK: Trả về thông tin người dùng hiện tại.

        {
            "id": 1,
            "email": "user@example.com",
            "fullName": "Nguyễn Văn A",
            "avatarUrl": "https://example.com/avatar.jpg",
            "bio": "Yêu thích nấu ăn",
            "hometown": "Hà Nội"
        }

        401 Unauthorized: Người dùng chưa đăng nhập hoặc token không hợp lệ.

        404 Not Found: Không tìm thấy người dùng (email trong token không tồn tại).

## 2. File Upload API

Endpoint quản lý việc upload và lưu trữ file/ảnh.

Controller: FileUploadController
Base Path: /api/upload

### 2.1 Upload một ảnh

    Method: POST

    Endpoint: /api/upload/image

    Mô tả: Upload một file ảnh lên server. File sẽ được lưu trong thư mục uploads/ và trả về URL để truy cập.

    Content-Type: multipart/form-data

    Form Data:

        file (required): File ảnh cần upload (jpg, png, gif)
        type (optional): Loại ảnh để phân loại thư mục (avatars, recipes, steps, general)

    Example request using curl:

    curl -X POST http://localhost:8080/api/upload/image \
      -F "file=@/path/to/image.jpg" \
      -F "type=recipes"

    Validation Rules:

        File không được rỗng
        File phải là ảnh (image/*)
        Kích thước tối đa: 5MB

    Responses:

        200 OK: Upload thành công, trả về thông tin file.

        {
            "success": true,
            "message": "Upload ảnh thành công",
            "fileName": "recipes/uuid-filename.jpg",
            "fileUrl": "http://localhost:8080/uploads/recipes/uuid-filename.jpg",
            "fileSize": 245678,
            "contentType": "image/jpeg"
        }

        400 Bad Request: File không hợp lệ.

        {
            "success": false,
            "message": "File phải là ảnh (jpg, png, gif)"
        }

        500 Internal Server Error: Lỗi khi lưu file.

### 2.2 Upload nhiều ảnh

    Method: POST

    Endpoint: /api/upload/images

    Mô tả: Upload nhiều file ảnh cùng lúc.

    Content-Type: multipart/form-data

    Form Data:

        files[] (required): Mảng các file ảnh cần upload
        type (optional): Loại ảnh để phân loại thư mục

    Responses:

        200 OK: Upload thành công.

        {
            "success": true,
            "message": "Upload thành công 3 ảnh",
            "files": [
                {
                    "fileName": "steps/uuid1.jpg",
                    "fileUrl": "http://localhost:8080/uploads/steps/uuid1.jpg",
                    "fileSize": 123456
                },
                {
                    "fileName": "steps/uuid2.jpg",
                    "fileUrl": "http://localhost:8080/uploads/steps/uuid2.jpg",
                    "fileSize": 234567
                }
            ]
        }

### 2.3 Truy cập ảnh đã upload

    Method: GET

    Endpoint: /uploads/{path}

    Mô tả: Truy cập ảnh đã được upload lên server.

    Parameters:

        path: Đường dẫn đến file (ví dụ: recipes/uuid-filename.jpg)

    Example: 

        http://localhost:8080/uploads/recipes/abc123.jpg
        http://localhost:8080/uploads/avatars/xyz789.png

    Responses:

        200 OK: Trả về file ảnh.

        404 Not Found: Không tìm thấy file.

### 2.4 Cấu trúc thư mục uploads

    uploads/
      ├── avatars/        # Ảnh đại diện người dùng
      ├── recipes/        # Ảnh công thức
      ├── steps/          # Ảnh các bước nấu
      └── general/        # Ảnh chung

## 3. Authentication API

Endpoint quản lý các quy trình xác thực như đăng ký, đăng nhập.

Controller: AuthController
Base Path: /api/auth
### 6.1 Gửi mã OTP

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

### 6.2 Đăng ký tài khoản mới

    Method: POST

    Endpoint: /api/auth/register

    Mô tả: Hoàn tất quá trình đăng ký bằng cách xác thực OTP và tạo tài khoản người dùng mới.

    Request Body:

    {
        "fullName": "Nguyễn Văn A",
        "email": "register.test@example.com",
        "password": "StrongPassword!123",
        "otp": "123456"
    }

    Validation Rules:

        fullName: Bắt buộc, không được để trống
        email: Bắt buộc, phải đúng định dạng email
        password: Bắt buộc, không được để trống
        otp: Bắt buộc, mã OTP đã được gửi đến email

    Responses:

        200 OK: Đăng ký tài khoản thành công.

        Đăng ký thành công

        400 Bad Request:

            OTP không hợp lệ hoặc đã hết hạn.

            Email đã tồn tại.

            Dữ liệu không hợp lệ (thiếu trường bắt buộc).

### 6.3 Đăng nhập

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

## 4. Recipe API

Endpoint quản lý các công thức nấu ăn.

Controller: RecipeController
Base Path: /api/recipes

### 6.1 Lấy danh sách tất cả công thức

    Method: GET

    Endpoint: /api/recipes/getRecipes

    Mô tả: Trả về danh sách tất cả công thức nấu ăn trong hệ thống. (Public - không cần xác thực)

    Responses:

        200 OK: Trả về một mảng chứa thông tin các công thức.

        [
            {
                "id": 1,
                "title": "Phở Bò Hà Nội",
                "imageUrl": "https://example.com/pho-bo.jpg",
                "servings": 4,
                "cookingTime": 180,
                "userId": 1,
                "userName": "Nguyễn Văn A",
                "userAvatar": "https://example.com/avatar.jpg",
                "ingredients": [
                    {
                        "id": 1,
                        "name": "Xương bò",
                        "quantity": "1",
                        "unit": "kg"
                    },
                    {
                        "id": 2,
                        "name": "Thịt bò",
                        "quantity": "500",
                        "unit": "g"
                    }
                ],
                "steps": [
                    {
                        "id": 1,
                        "stepNumber": 1,
                        "title": "Chuẩn bị nguyên liệu",
                        "images": [
                            {
                                "id": 1,
                                "imageUrl": "https://example.com/step1.jpg",
                                "orderNumber": 1
                            }
                        ]
                    },
                    {
                        "id": 2,
                        "stepNumber": 2,
                        "title": "Ninh nước dùng",
                        "images": []
                    }
                ],
                "createdAt": "2025-10-15T10:30:00",
                "updatedAt": "2025-10-15T10:30:00"
            }
        ]

### 6.2 Lấy thông tin công thức theo ID

    Method: GET

    Endpoint: /api/recipes/{id}

    Mô tả: Trả về thông tin chi tiết của một công thức dựa trên ID. (Public - không cần xác thực)

    Parameters:

        id (path variable, Long): ID của công thức cần lấy.

    Responses:

        200 OK: Trả về thông tin chi tiết của công thức.

        {
            "id": 1,
            "title": "Phở Bò Hà Nội",
            "imageUrl": "https://example.com/pho-bo.jpg",
            "servings": 4,
            "cookingTime": 180,
            "userId": 1,
            "userName": "Nguyễn Văn A",
            "userAvatar": "https://example.com/avatar.jpg",
            "ingredients": [...],
            "steps": [...],
            "createdAt": "2025-10-15T10:30:00",
            "updatedAt": "2025-10-15T10:30:00"
        }

        404 Not Found: Không tìm thấy công thức với ID đã cung cấp.

### 6.3 Lấy công thức theo User ID

    Method: GET

    Endpoint: /api/recipes/user/{userId}

    Mô tả: Trả về danh sách tất cả công thức của một người dùng cụ thể. (Public - không cần xác thực)

    Parameters:

        userId (path variable, Long): ID của người dùng.

    Example: /api/recipes/user/1

    Responses:

        200 OK: Trả về mảng các công thức của người dùng.

        [
            {
                "id": 1,
                "title": "Phở Bò Hà Nội",
                "imageUrl": "https://example.com/pho-bo.jpg",
                "servings": 4,
                "cookingTime": 180,
                ...
            },
            {
                "id": 2,
                "title": "Bún Chả Hà Nội",
                ...
            }
        ]

### 6.4 Lấy công thức của tôi

    Method: GET

    Endpoint: /api/recipes/my-recipes

    Mô tả: Trả về danh sách tất cả công thức của người dùng hiện tại (dựa trên JWT token). (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Responses:

        200 OK: Trả về mảng các công thức của người dùng hiện tại.

        401 Unauthorized: Người dùng chưa đăng nhập hoặc token không hợp lệ.

### 6.5 Tìm kiếm công thức theo tiêu đề

    Method: GET

    Endpoint: /api/recipes/search

    Mô tả: Tìm kiếm công thức theo từ khóa trong tiêu đề (không phân biệt chữ hoa/thường). Tự động lưu lịch sử tìm kiếm nếu user đã đăng nhập. (Public - không cần xác thực)

    Query Parameters:

        title (String): Từ khóa cần tìm kiếm trong tiêu đề công thức.

    Example: /api/recipes/search?title=phở

    Note: Nếu user đã đăng nhập, query sẽ tự động được lưu vào lịch sử tìm kiếm.

    Responses:

        200 OK: Trả về mảng các công thức có tiêu đề chứa từ khóa.

        [
            {
                "id": 1,
                "title": "Phở Bò Hà Nội",
                ...
            },
            {
                "id": 5,
                "title": "Phở Gà",
                ...
            }
        ]

### 6.6 Tạo công thức mới

    Method: POST

    Endpoint: /api/recipes

    Mô tả: Tạo một công thức nấu ăn mới. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>
        Content-Type: application/json

    Request Body:

    {
        "title": "Cơm Chiên Trứng",
        "imageUrl": "https://example.com/com-chien.jpg",
        "servings": 2,
        "cookingTime": 15,
        "ingredients": [
            {
                "name": "Cơm nguội",
                "quantity": "1",
                "unit": "chén"
            },
            {
                "name": "Trứng gà",
                "quantity": "2",
                "unit": "quả"
            },
            {
                "name": "Hành lá",
                "quantity": "2",
                "unit": "cây"
            }
        ],
        "steps": [
            {
                "stepNumber": 1,
                "title": "Chuẩn bị",
                "images": []
            },
            {
                "stepNumber": 2,
                "title": "Chiên",
                "images": [
                    {
                        "imageUrl": "https://example.com/step2.jpg",
                        "orderNumber": 1
                    }
                ]
            }
        ]
    }

    Validation Rules:

        title: Bắt buộc, không được để trống
        servings: Bắt buộc, phải > 0
        cookingTime: Tùy chọn, nếu có phải > 0
        ingredients[].name: Bắt buộc cho mỗi nguyên liệu
        steps[].stepNumber: Bắt buộc cho mỗi bước
        steps[].title: Bắt buộc cho mỗi bước

    Responses:

        201 Created: Tạo công thức thành công, trả về thông tin công thức đã tạo.

        {
            "id": 3,
            "title": "Cơm Chiên Trứng",
            "imageUrl": "https://example.com/com-chien.jpg",
            "servings": 2,
            "cookingTime": 15,
            "userId": 1,
            "userName": "Nguyễn Văn A",
            "userAvatar": "https://example.com/avatar.jpg",
            "ingredients": [...],
            "steps": [...],
            "createdAt": "2025-10-15T11:00:00",
            "updatedAt": "2025-10-15T11:00:00"
        }

        400 Bad Request: Dữ liệu đầu vào không hợp lệ (thiếu trường bắt buộc, giá trị không hợp lệ).

        401 Unauthorized: Người dùng chưa đăng nhập hoặc token không hợp lệ.

### 6.6.1 Tạo công thức với User ID (Admin)

    Method: POST

    Endpoint: /api/recipes/admin/create

    Mô tả: Tạo một công thức nấu ăn mới cho một user cụ thể. API này cho phép admin tạo công thức thay mặt cho bất kỳ user nào.

    Headers:

        Content-Type: application/json

    Request Body:

    {
        "userId": 5,
        "title": "Cơm Chiên Trứng",
        "imageUrl": "https://example.com/com-chien.jpg",
        "servings": 2,
        "cookingTime": 15,
        "ingredients": [
            {
                "name": "Cơm nguội",
                "quantity": "1",
                "unit": "chén"
            },
            {
                "name": "Trứng gà",
                "quantity": "2",
                "unit": "quả"
            },
            {
                "name": "Hành lá",
                "quantity": "2",
                "unit": "cây"
            }
        ],
        "steps": [
            {
                "stepNumber": 1,
                "title": "Chuẩn bị",
                "images": []
            },
            {
                "stepNumber": 2,
                "title": "Chiên",
                "images": [
                    {
                        "imageUrl": "https://example.com/step2.jpg",
                        "orderNumber": 1
                    }
                ]
            }
        ]
    }

    Validation Rules:

        userId: Bắt buộc, phải > 0, user phải tồn tại trong hệ thống
        title: Bắt buộc, không được để trống
        servings: Bắt buộc, phải > 0
        cookingTime: Tùy chọn, nếu có phải > 0
        ingredients[].name: Bắt buộc cho mỗi nguyên liệu
        steps[].stepNumber: Bắt buộc cho mỗi bước
        steps[].title: Bắt buộc cho mỗi bước

    Responses:

        201 Created: Tạo công thức thành công, trả về thông tin công thức đã tạo.

        {
            "id": 3,
            "title": "Cơm Chiên Trứng",
            "imageUrl": "https://example.com/com-chien.jpg",
            "servings": 2,
            "cookingTime": 15,
            "userId": 5,
            "userName": "Nguyễn Văn B",
            "userAvatar": "https://example.com/avatar.jpg",
            "ingredients": [...],
            "steps": [...],
            "createdAt": "2025-10-15T11:00:00",
            "updatedAt": "2025-10-15T11:00:00"
        }

        400 Bad Request: Dữ liệu đầu vào không hợp lệ (thiếu trường bắt buộc, giá trị không hợp lệ, user không tồn tại).

### 6.7 Cập nhật công thức

    Method: PUT

    Endpoint: /api/recipes/{id}

    Mô tả: Cập nhật thông tin của một công thức hiện có. Chỉ người tạo công thức mới có quyền cập nhật. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>
        Content-Type: application/json

    Parameters:

        id (path variable, Long): ID của công thức cần cập nhật.

    Request Body: Giống như khi tạo mới (xem 3.6)

    {
        "title": "Cơm Chiên Trứng (Updated)",
        "imageUrl": "https://example.com/com-chien-new.jpg",
        "servings": 3,
        "cookingTime": 20,
        "ingredients": [...],
        "steps": [...]
    }

    Responses:

        200 OK: Cập nhật thành công, trả về thông tin công thức sau khi cập nhật.

        400 Bad Request: Dữ liệu đầu vào không hợp lệ.

        401 Unauthorized: Người dùng chưa đăng nhập.

        403 Forbidden: Người dùng không có quyền sửa công thức này (không phải người tạo).

        404 Not Found: Không tìm thấy công thức để cập nhật.

### 6.8 Xóa công thức

    Method: DELETE

    Endpoint: /api/recipes/{id}

    Mô tả: Xóa một công thức khỏi hệ thống. Chỉ người tạo công thức mới có quyền xóa. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        id (path variable, Long): ID của công thức cần xóa.

    Responses:

        200 OK: Xóa công thức thành công.

        "Xóa công thức thành công"

        401 Unauthorized: Người dùng chưa đăng nhập.

        403 Forbidden: Người dùng không có quyền xóa công thức này (không phải người tạo).

        404 Not Found: Không tìm thấy công thức để xóa.

### 6.9 Like công thức

    Method: POST

    Endpoint: /api/recipes/{id}/like

    Mô tả: Like một công thức. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        id (path variable, Long): ID của công thức cần like.

    Response Body:

```json
{
  "message": "Đã thích công thức",
  "liked": true,
  "likesCount": 15
}
```

    Responses:

        200 OK: Like thành công.

        409 Conflict: Người dùng đã like công thức này rồi.

        401 Unauthorized: Người dùng chưa đăng nhập.

        404 Not Found: Không tìm thấy công thức.

### 6.10 Unlike công thức

    Method: DELETE

    Endpoint: /api/recipes/{id}/like

    Mô tả: Unlike một công thức đã like trước đó. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        id (path variable, Long): ID của công thức cần unlike.

    Response Body:

```json
{
  "message": "Đã bỏ thích công thức",
  "liked": false,
  "likesCount": 14
}
```

    Responses:

        200 OK: Unlike thành công.

        404 Not Found: Người dùng chưa like công thức này.

        401 Unauthorized: Người dùng chưa đăng nhập.

### 6.11 Toggle like công thức

    Method: POST

    Endpoint: /api/recipes/{id}/toggle-like

    Mô tả: Tự động like/unlike công thức (nếu đã like thì unlike, nếu chưa like thì like). (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        id (path variable, Long): ID của công thức.

    Response Body:

```json
{
  "message": "Đã thích công thức",
  "liked": true,
  "likesCount": 15
}
```

    Responses:

        200 OK: Toggle thành công.

        401 Unauthorized: Người dùng chưa đăng nhập.

        404 Not Found: Không tìm thấy công thức.

### 6.12 Kiểm tra trạng thái like

    Method: GET

    Endpoint: /api/recipes/{id}/is-liked

    Mô tả: Kiểm tra xem người dùng hiện tại đã like công thức này chưa. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        id (path variable, Long): ID của công thức cần kiểm tra.

    Response Body:

```json
{
  "recipeId": 1,
  "isLiked": true
}
```

    Responses:

        200 OK: Trả về trạng thái like.

        401 Unauthorized: Người dùng chưa đăng nhập.

### 6.13 Lấy danh sách công thức đã like

    Method: GET

    Endpoint: /api/recipes/liked

    Mô tả: Lấy danh sách ID của tất cả công thức mà người dùng hiện tại đã like. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Response Body:

```json
[1, 5, 12, 28, 42]
```

    Responses:

        200 OK: Trả về danh sách recipe IDs.

        401 Unauthorized: Người dùng chưa đăng nhập.

### 6.14 Lưu công thức (Bookmark)

    Method: POST

    Endpoint: /api/recipes/{id}/bookmark

    Mô tả: Lưu/Bookmark một công thức để xem sau. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        id (path variable, Long): ID của công thức cần lưu.

    Response Body:

```json
{
  "message": "Đã lưu công thức",
  "bookmarked": true,
  "bookmarksCount": 15
}
```

    Responses:

        200 OK: Lưu công thức thành công.

        409 Conflict: Người dùng đã lưu công thức này rồi.

        401 Unauthorized: Người dùng chưa đăng nhập.

        404 Not Found: Không tìm thấy công thức.

### 6.15 Bỏ lưu công thức (Unbookmark)

    Method: DELETE

    Endpoint: /api/recipes/{id}/bookmark

    Mô tả: Bỏ lưu một công thức đã bookmark. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        id (path variable, Long): ID của công thức cần bỏ lưu.

    Response Body:

```json
{
  "message": "Đã bỏ lưu công thức",
  "bookmarked": false,
  "bookmarksCount": 14
}
```

    Responses:

        200 OK: Bỏ lưu công thức thành công.

        404 Not Found: Người dùng chưa lưu công thức này.

        401 Unauthorized: Người dùng chưa đăng nhập.

### 6.16 Toggle Bookmark

    Method: POST

    Endpoint: /api/recipes/{id}/toggle-bookmark

    Mô tả: Chuyển đổi trạng thái bookmark (nếu chưa lưu thì lưu, nếu đã lưu thì bỏ lưu). (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        id (path variable, Long): ID của công thức.

    Response Body:

```json
{
  "message": "Đã lưu công thức",
  "bookmarked": true,
  "bookmarksCount": 15
}
```

    Responses:

        200 OK: Toggle thành công, trả về trạng thái mới.

        401 Unauthorized: Người dùng chưa đăng nhập.

        404 Not Found: Không tìm thấy công thức.

### 6.17 Kiểm tra trạng thái Bookmark

    Method: GET

    Endpoint: /api/recipes/{id}/is-bookmarked

    Mô tả: Kiểm tra xem người dùng hiện tại đã bookmark công thức này chưa.

    Headers (Optional):

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        id (path variable, Long): ID của công thức cần kiểm tra.

    Response Body:

```json
{
  "bookmarked": true,
  "bookmarksCount": 15
}
```

    Responses:

        200 OK: Trả về trạng thái bookmark (nếu chưa đăng nhập thì bookmarked luôn là false).

        404 Not Found: Không tìm thấy công thức.

### 6.18 Lấy danh sách công thức đã lưu

    Method: GET

    Endpoint: /api/recipes/bookmarked

    Mô tả: Lấy danh sách ID của tất cả công thức mà người dùng hiện tại đã bookmark. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Response Body:

```json
[2, 8, 15, 23, 47]
```

    Responses:

        200 OK: Trả về danh sách recipe IDs đã bookmark.

        401 Unauthorized: Người dùng chưa đăng nhập.

### 6.19 Thêm bình luận cho công thức

    Method: POST

    Endpoint: /api/recipes/{recipeId}/comments

    Mô tả: Thêm bình luận hoặc trả lời bình luận cho một công thức. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        recipeId (path variable, Long): ID của công thức.

    Request Body:

```json
{
  "comment": "Công thức rất ngon và dễ làm!",
  "parentCommentId": null
}
```

    Responses:

        201 Created: Thêm bình luận thành công.

        400 Bad Request: Dữ liệu không hợp lệ.

        401 Unauthorized: Người dùng chưa đăng nhập.

### 6.20 Lấy danh sách bình luận

    Method: GET

    Endpoint: /api/recipes/{recipeId}/comments

    Mô tả: Lấy tất cả bình luận cho một công thức (bao gồm cả replies).

    Parameters:

        recipeId (path variable, Long): ID của công thức.

    Response Body:

```json
[
  {
    "id": 1,
    "userId": 5,
    "userName": "Nguyễn Văn A",
    "userAvatar": "https://example.com/avatar.jpg",
    "recipeId": 10,
    "comment": "Công thức rất tuyệt vời!",
    "parentCommentId": null,
    "replies": [
      {
        "id": 2,
        "userId": 8,
        "userName": "Trần Thị B",
        "comment": "Tôi đồng ý!",
        "parentCommentId": 1,
        "replies": []
      }
    ],
    "createdAt": "2024-01-20T10:30:00",
    "updatedAt": "2024-01-20T10:30:00"
  }
]
```

    Responses:

        200 OK: Trả về danh sách bình luận.

        404 Not Found: Không tìm thấy công thức.

### 6.21 Cập nhật bình luận

    Method: PUT

    Endpoint: /api/recipes/{recipeId}/comments/{commentId}

    Mô tả: Cập nhật nội dung bình luận của mình. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        recipeId (path variable, Long): ID của công thức.
        commentId (path variable, Long): ID của bình luận cần sửa.

    Request Body:

```json
{
  "comment": "Nội dung đã được cập nhật"
}
```

    Responses:

        200 OK: Cập nhật thành công.

        400 Bad Request: Dữ liệu không hợp lệ.

        401 Unauthorized: Người dùng chưa đăng nhập.

        403 Forbidden: Không có quyền sửa bình luận này.

### 6.22 Xóa bình luận

    Method: DELETE

    Endpoint: /api/recipes/{recipeId}/comments/{commentId}

    Mô tả: Xóa bình luận của mình. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        recipeId (path variable, Long): ID của công thức.
        commentId (path variable, Long): ID của bình luận cần xóa.

    Responses:

        200 OK: "Xóa bình luận thành công"

        401 Unauthorized: Người dùng chưa đăng nhập.

        403 Forbidden: Không có quyền xóa bình luận này.

### 6.23 Đánh giá công thức

    Method: POST

    Endpoint: /api/recipes/{recipeId}/ratings

    Mô tả: Đánh giá công thức (1-5 sao). Nếu đã đánh giá trước đó sẽ cập nhật đánh giá mới. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        recipeId (path variable, Long): ID của công thức.

    Request Body:

```json
{
  "rating": 5
}
```

    Response Body:

```json
{
  "rating": {
    "id": 15,
    "userId": 5,
    "userName": "Nguyễn Văn A",
    "userAvatar": "https://example.com/avatar.jpg",
    "recipeId": 10,
    "rating": 5,
    "createdAt": "2024-01-20T10:30:00",
    "updatedAt": "2024-01-20T10:30:00"
  },
  "averageRating": 4.5,
  "ratingsCount": 25,
  "message": "Đánh giá thành công"
}
```

    Responses:

        200 OK: Đánh giá thành công.

        400 Bad Request: Dữ liệu không hợp lệ (rating phải từ 1-5).

        401 Unauthorized: Người dùng chưa đăng nhập.

### 6.24 Lấy đánh giá của tôi

    Method: GET

    Endpoint: /api/recipes/{recipeId}/ratings/my-rating

    Mô tả: Lấy đánh giá của người dùng hiện tại cho công thức. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        recipeId (path variable, Long): ID của công thức.

    Response Body:

```json
{
  "id": 15,
  "userId": 5,
  "userName": "Nguyễn Văn A",
  "recipeId": 10,
  "rating": 5,
  "createdAt": "2024-01-20T10:30:00",
  "updatedAt": "2024-01-20T10:30:00"
}
```

    Responses:

        200 OK: Trả về đánh giá của người dùng.

        404 Not Found: Người dùng chưa đánh giá công thức này.

        401 Unauthorized: Người dùng chưa đăng nhập.

### 6.25 Xóa đánh giá

    Method: DELETE

    Endpoint: /api/recipes/{recipeId}/ratings

    Mô tả: Xóa đánh giá của mình cho công thức. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        recipeId (path variable, Long): ID của công thức.

    Responses:

        200 OK: "Xóa đánh giá thành công"

        400 Bad Request: Người dùng chưa đánh giá công thức này.

        401 Unauthorized: Người dùng chưa đăng nhập.

### 6.26 Lấy thống kê đánh giá

    Method: GET

    Endpoint: /api/recipes/{recipeId}/ratings/stats

    Mô tả: Lấy thống kê đánh giá của công thức (average rating, rating distribution).

    Parameters:

        recipeId (path variable, Long): ID của công thức.

    Response Body:

```json
{
  "averageRating": 4.5,
  "ratingsCount": 100,
  "ratingDistribution": {
    "5": 60,
    "4": 25,
    "3": 10,
    "2": 3,
    "1": 2
  }
}
```

    Responses:

        200 OK: Trả về thống kê đánh giá.

        404 Not Found: Không tìm thấy công thức.

### 6.27 Lấy tất cả đánh giá

    Method: GET

    Endpoint: /api/recipes/{recipeId}/ratings

    Mô tả: Lấy danh sách tất cả đánh giá cho một công thức (bao gồm thông tin người dùng).

    Parameters:

        recipeId (path variable, Long): ID của công thức.

    Response Body:

```json
[
  {
    "id": 15,
    "userId": 5,
    "userName": "Nguyễn Văn A",
    "userAvatar": "https://example.com/avatar.jpg",
    "recipeId": 10,
    "rating": 5,
    "createdAt": "2024-01-20T10:30:00",
    "updatedAt": "2024-01-20T10:30:00"
  },
  {
    "id": 16,
    "userId": 8,
    "userName": "Trần Thị B",
    "userAvatar": "https://example.com/avatar2.jpg",
    "recipeId": 10,
    "rating": 4,
    "createdAt": "2024-01-21T14:20:00",
    "updatedAt": "2024-01-21T14:20:00"
  }
]
```

    Responses:

        200 OK: Trả về danh sách đánh giá.

        404 Not Found: Không tìm thấy công thức.

## 7. Notification API

Endpoint quản lý thông báo cho người dùng.

Controller: NotificationController
Base Path: /api/notifications

### 7.1 Lấy tất cả thông báo

    Method: GET

    Endpoint: /api/notifications

    Mô tả: Lấy danh sách tất cả thông báo của người dùng hiện tại, sắp xếp theo thời gian mới nhất. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Response Body:

```json
[
  {
    "id": 1,
    "userId": 5,
    "type": "LIKE",
    "actorId": 8,
    "actorName": "Nguyễn Văn A",
    "actorAvatar": "https://example.com/avatar.jpg",
    "recipeId": 10,
    "recipeTitle": "Phở Bò Hà Nội",
    "recipeImage": "https://example.com/pho.jpg",
    "commentId": null,
    "message": "Nguyễn Văn A đã thích công thức \"Phở Bò Hà Nội\" của bạn",
    "isRead": false,
    "createdAt": "2025-10-30T10:30:00"
  },
  {
    "id": 2,
    "type": "COMMENT",
    "actorName": "Trần Thị B",
    "message": "Trần Thị B đã bình luận về công thức \"Phở Bò Hà Nội\" của bạn",
    "isRead": true,
    "createdAt": "2025-10-30T09:15:00"
  }
]
```

    Responses:

        200 OK: Trả về danh sách thông báo.

        401 Unauthorized: Người dùng chưa đăng nhập.

### 7.2 Lấy thông báo chưa đọc

    Method: GET

    Endpoint: /api/notifications/unread

    Mô tả: Lấy danh sách các thông báo chưa đọc của người dùng hiện tại. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Response Body: Giống như 7.1 nhưng chỉ trả về thông báo có isRead = false.

    Responses:

        200 OK: Trả về danh sách thông báo chưa đọc.

        401 Unauthorized: Người dùng chưa đăng nhập.

### 7.3 Đếm số thông báo chưa đọc

    Method: GET

    Endpoint: /api/notifications/unread/count

    Mô tả: Lấy số lượng thông báo chưa đọc của người dùng hiện tại. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Response Body:

```json
{
  "count": 5
}
```

    Responses:

        200 OK: Trả về số lượng thông báo chưa đọc.

        401 Unauthorized: Người dùng chưa đăng nhập.

### 7.4 Đánh dấu thông báo đã đọc

    Method: PUT

    Endpoint: /api/notifications/{id}/read

    Mô tả: Đánh dấu một thông báo cụ thể là đã đọc. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        id (path variable, Long): ID của thông báo.

    Responses:

        200 OK: "Đã đánh dấu thông báo là đã đọc"

        404 Not Found: "Không tìm thấy thông báo"

        401 Unauthorized: Người dùng chưa đăng nhập.

### 7.5 Đánh dấu tất cả đã đọc

    Method: PUT

    Endpoint: /api/notifications/read-all

    Mô tả: Đánh dấu tất cả thông báo chưa đọc là đã đọc. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Response Body:

```json
{
  "message": "Đã đánh dấu tất cả thông báo là đã đọc",
  "count": 5
}
```

    Responses:

        200 OK: Đánh dấu thành công, trả về số lượng thông báo đã được đánh dấu.

        401 Unauthorized: Người dùng chưa đăng nhập.

### 7.6 Xóa thông báo

    Method: DELETE

    Endpoint: /api/notifications/{id}

    Mô tả: Xóa một thông báo cụ thể. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Parameters:

        id (path variable, Long): ID của thông báo cần xóa.

    Responses:

        200 OK: "Đã xóa thông báo"

        401 Unauthorized: Người dùng chưa đăng nhập.

### 7.7 Xóa tất cả thông báo

    Method: DELETE

    Endpoint: /api/notifications

    Mô tả: Xóa tất cả thông báo của người dùng hiện tại. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Responses:

        200 OK: "Đã xóa tất cả thông báo"

        401 Unauthorized: Người dùng chưa đăng nhập.

## 8. User Follow API

Endpoint quản lý chức năng theo dõi (follow) người dùng.

Controller: UserFollowController
Base Path: /api/users

### 8.1 Follow một người dùng

    Method: POST

    Endpoint: /api/users/{userId}/follow

    Mô tả: Người dùng hiện tại theo dõi người dùng khác. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Path Parameters:

        userId (Long): ID của người dùng muốn theo dõi.

    Responses:

        200 OK: Theo dõi thành công.

```json
{
  "message": "Successfully followed user"
}
```

        400 Bad Request: 
            - Cannot follow yourself
            - Already following this user
            - User not found

```json
{
  "error": "Cannot follow yourself"
}
```

### 8.2 Unfollow một người dùng

    Method: DELETE

    Endpoint: /api/users/{userId}/follow

    Mô tả: Hủy theo dõi một người dùng. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Path Parameters:

        userId (Long): ID của người dùng muốn hủy theo dõi.

    Responses:

        200 OK: Hủy theo dõi thành công.

```json
{
  "message": "Successfully unfollowed user"
}
```

        400 Bad Request: Not following this user.

```json
{
  "error": "Not following this user"
}
```

### 8.3 Lấy danh sách người đang theo dõi

    Method: GET

    Endpoint: /api/users/{userId}/following

    Mô tả: Lấy danh sách những người mà user đang theo dõi. (Public)

    Path Parameters:

        userId (Long): ID của người dùng.

    Responses:

        200 OK: Trả về danh sách user.

```json
[
  {
    "id": 2,
    "email": "user2@example.com",
    "fullName": "Nguyen Van B",
    "avatarUrl": "https://example.com/avatar2.jpg",
    "bio": "Food lover",
    "hometown": "Hanoi",
    "provider": "local",
    "followersCount": 50,
    "followingCount": 30
  },
  {
    "id": 3,
    "email": "user3@example.com",
    "fullName": "Tran Thi C",
    "avatarUrl": null,
    "bio": null,
    "hometown": "HCM",
    "provider": "google",
    "followersCount": 120,
    "followingCount": 85
  }
]
```

### 8.4 Lấy danh sách người theo dõi (followers)

    Method: GET

    Endpoint: /api/users/{userId}/followers

    Mô tả: Lấy danh sách những người đang theo dõi user. (Public)

    Path Parameters:

        userId (Long): ID của người dùng.

    Responses:

        200 OK: Trả về danh sách user.

```json
[
  {
    "id": 4,
    "email": "user4@example.com",
    "fullName": "Le Van D",
    "avatarUrl": "https://example.com/avatar4.jpg",
    "bio": "Chef",
    "hometown": "Da Nang",
    "provider": "local",
    "followersCount": 200,
    "followingCount": 150
  }
]
```

### 8.5 Kiểm tra xem có đang follow không

    Method: GET

    Endpoint: /api/users/{userId}/is-following

    Mô tả: Kiểm tra xem người dùng hiện tại có đang follow user này không. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Path Parameters:

        userId (Long): ID của người dùng cần kiểm tra.

    Responses:

        200 OK:

```json
{
  "isFollowing": true
}
```

### 8.6 Lấy thống kê follow

    Method: GET

    Endpoint: /api/users/{userId}/follow-stats

    Mô tả: Lấy số lượng followers và following của một user. (Public)

    Path Parameters:

        userId (Long): ID của người dùng.

    Responses:

        200 OK:

```json
{
  "followersCount": 150,
  "followingCount": 80
}
```

### 8.7 Lấy feed từ người mình follow

    Method: GET

    Endpoint: /api/recipes/following-feed

    Mô tả: Lấy danh sách công thức từ những người mà user đang follow, sắp xếp theo thời gian đăng mới nhất. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Responses:

        200 OK: Trả về danh sách recipes từ những người đang follow.

```json
[
  {
    "id": 101,
    "title": "Phở Bò Hà Nội",
    "description": "Món phở truyền thống",
    "imageUrl": "https://example.com/pho.jpg",
    "userId": 2,
    "userName": "Nguyen Van B",
    "userAvatar": "https://example.com/avatar2.jpg",
    "cookingTime": 120,
    "servings": 4,
    "difficulty": "medium",
    "category": "Vietnamese",
    "averageRating": 4.5,
    "totalRatings": 10,
    "totalLikes": 25,
    "totalBookmarks": 15,
    "totalComments": 8,
    "isLikedByCurrentUser": true,
    "isBookmarkedByCurrentUser": false,
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

## 9. Search History API

Endpoint quản lý lịch sử tìm kiếm của người dùng.

Controller: SearchHistoryController
Base Path: /api/search-history

### 9.1 Lấy lịch sử tìm kiếm

    Method: GET

    Endpoint: /api/search-history

    Mô tả: Lấy danh sách lịch sử tìm kiếm của người dùng hiện tại. Mặc định trả về các query duy nhất (không trùng lặp). (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Query Parameters:

        limit (Integer, optional): Số lượng kết quả tối đa (mặc định: 20).
        showAll (Boolean, optional): Trả về tất cả lịch sử (bao gồm trùng lặp) hay chỉ query duy nhất (mặc định: false).

    Example: 
        /api/search-history?limit=10
        /api/search-history?showAll=true&limit=50

    Response Body (showAll=false - mặc định):

```json
{
  "total": 5,
  "queries": [
    "phở bò",
    "cơm chiên",
    "bún chả",
    "bánh mì",
    "gỏi cuốn"
  ]
}
```

    Response Body (showAll=true):

```json
{
  "total": 10,
  "showing": 10,
  "history": [
    {
      "id": 15,
      "userId": 5,
      "searchQuery": "phở bò",
      "searchedAt": "2025-10-28T14:30:00"
    },
    {
      "id": 14,
      "userId": 5,
      "searchQuery": "cơm chiên",
      "searchedAt": "2025-10-28T14:25:00"
    },
    {
      "id": 13,
      "userId": 5,
      "searchQuery": "phở bò",
      "searchedAt": "2025-10-28T14:20:00"
    }
  ]
}
```

    Responses:

        200 OK: Trả về lịch sử tìm kiếm.

        401 Unauthorized: Người dùng chưa đăng nhập.

### 9.2 Lưu lịch sử tìm kiếm

    Method: POST

    Endpoint: /api/search-history

    Mô tả: Lưu một query tìm kiếm vào lịch sử của người dùng hiện tại (thủ công). (Requires Authentication)

    Note: Thông thường lịch sử sẽ tự động được lưu khi user gọi API search recipes. Endpoint này để lưu thủ công nếu cần.

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Request Body:

```json
{
  "query": "phở bò"
}
```

    Responses:

        201 Created: Lưu lịch sử thành công.

```json
{
  "id": 16,
  "userId": 5,
  "searchQuery": "phở bò",
  "searchedAt": "2025-10-28T14:35:00"
}
```

        400 Bad Request: Query không hợp lệ (trống hoặc null).

        401 Unauthorized: Người dùng chưa đăng nhập.

### 9.3 Xóa toàn bộ lịch sử tìm kiếm

    Method: DELETE

    Endpoint: /api/search-history

    Mô tả: Xóa tất cả lịch sử tìm kiếm của người dùng hiện tại. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Responses:

        200 OK: "Đã xóa toàn bộ lịch sử tìm kiếm"

        401 Unauthorized: Người dùng chưa đăng nhập.

### 9.4 Xóa một query cụ thể

    Method: DELETE

    Endpoint: /api/search-history/query

    Mô tả: Xóa một query tìm kiếm cụ thể khỏi lịch sử của người dùng hiện tại. Sẽ xóa tất cả entries của query đó (nếu có nhiều lần search). (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Query Parameters:

        query (String): Query cần xóa.

    Example: /api/search-history/query?query=phở bò

    Responses:

        200 OK: "Đã xóa query: phở bò"

        400 Bad Request: Query không hợp lệ (trống hoặc null).

        401 Unauthorized: Người dùng chưa đăng nhập.

### 9.5 Thống kê lịch sử tìm kiếm

    Method: GET

    Endpoint: /api/search-history/stats

    Mô tả: Lấy thông tin thống kê về lịch sử tìm kiếm của người dùng. (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Response Body:

```json
{
  "totalSearches": 25,
  "uniqueQueries": 8,
  "recentQueries": [
    "phở bò",
    "cơm chiên",
    "bún chả",
    "bánh mì",
    "gỏi cuốn"
  ]
}
```

    Responses:

        200 OK: Trả về thống kê lịch sử tìm kiếm.

        401 Unauthorized: Người dùng chưa đăng nhập.

## 10. Database Schema

### 10.1 Bảng recipes

    id: BIGINT (Primary Key, Auto Increment)
    title: VARCHAR(255) NOT NULL
    image_url: VARCHAR(500)
    servings: INT NOT NULL
    cooking_time: INT
    user_id: BIGINT NOT NULL (Foreign Key -> users.id)
    likes_count: INT DEFAULT 0
    bookmarks_count: INT DEFAULT 0
    average_rating: DECIMAL(3,2) DEFAULT 0.00
    ratings_count: INT DEFAULT 0
    comments_count: INT DEFAULT 0
    created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### 10.2 Bảng ingredients

    id: BIGINT (Primary Key, Auto Increment)
    recipe_id: BIGINT NOT NULL (Foreign Key -> recipes.id)
    name: VARCHAR(255) NOT NULL
    quantity: VARCHAR(50)
    unit: VARCHAR(50)

### 10.3 Bảng recipe_steps

    id: BIGINT (Primary Key, Auto Increment)
    recipe_id: BIGINT NOT NULL (Foreign Key -> recipes.id)
    step_number: INT NOT NULL
    title: TEXT NOT NULL

### 10.4 Bảng step_images

    id: BIGINT (Primary Key, Auto Increment)
    step_id: BIGINT NOT NULL (Foreign Key -> recipe_steps.id)
    image_url: VARCHAR(500) NOT NULL
    order_number: INT

### 10.5 Bảng recipe_likes

    id: BIGINT (Primary Key, Auto Increment)
    user_id: BIGINT NOT NULL (Foreign Key -> users.id)
    recipe_id: BIGINT NOT NULL (Foreign Key -> recipes.id)
    created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    UNIQUE KEY: unique_user_recipe_like (user_id, recipe_id)

### 10.6 Bảng recipe_bookmarks

    id: BIGINT (Primary Key, Auto Increment)
    user_id: BIGINT NOT NULL (Foreign Key -> users.id)
    recipe_id: BIGINT NOT NULL (Foreign Key -> recipes.id)
    created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    UNIQUE KEY: unique_user_recipe_bookmark (user_id, recipe_id)

### 10.7 Bảng recipe_comments

    id: BIGINT (Primary Key, Auto Increment)
    user_id: BIGINT NOT NULL (Foreign Key -> users.id)
    recipe_id: BIGINT NOT NULL (Foreign Key -> recipes.id)
    comment: TEXT NOT NULL
    parent_comment_id: BIGINT NULL (Foreign Key -> recipe_comments.id, for replies)
    created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### 10.8 Bảng recipe_ratings

    id: BIGINT (Primary Key, Auto Increment)
    user_id: BIGINT NOT NULL (Foreign Key -> users.id)
    recipe_id: BIGINT NOT NULL (Foreign Key -> recipes.id)
    rating: INT NOT NULL CHECK (rating >= 1 AND rating <= 5)
    created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    UNIQUE KEY: unique_user_recipe_rating (user_id, recipe_id)

### 10.9 Bảng notifications

    id: BIGINT (Primary Key, Auto Increment)
    user_id: BIGINT NOT NULL (Foreign Key -> users.id)
    type: VARCHAR(50) NOT NULL (LIKE, COMMENT, RATING, REPLY, BOOKMARK)
    actor_id: BIGINT NOT NULL (Foreign Key -> users.id, người thực hiện hành động)
    recipe_id: BIGINT NULL (Foreign Key -> recipes.id)
    comment_id: BIGINT NULL (Foreign Key -> recipe_comments.id)
    message: VARCHAR(500) NOT NULL
    is_read: BOOLEAN NOT NULL DEFAULT FALSE
    created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    INDEX: idx_user_id (user_id)
    INDEX: idx_user_read (user_id, is_read)
    INDEX: idx_created_at (created_at)
    INDEX: idx_type (type)
    INDEX: idx_user_created (user_id, created_at DESC)

### 10.10 Bảng search_history

    id: BIGINT (Primary Key, Auto Increment)
    user_id: BIGINT NOT NULL (Foreign Key -> users.id)
    search_query: VARCHAR(255) NOT NULL
    searched_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    INDEX: idx_user_id (user_id)
    INDEX: idx_searched_at (searched_at)
    INDEX: idx_user_searched (user_id, searched_at DESC)

### 10.11 Bảng user_follows

    id: BIGINT (Primary Key, Auto Increment)
    follower_id: BIGINT NOT NULL (Foreign Key -> users.id) - Người theo dõi
    following_id: BIGINT NOT NULL (Foreign Key -> users.id) - Người được theo dõi
    created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    UNIQUE KEY: uk_user_follows_unique (follower_id, following_id)
    CHECK: chk_user_follows_no_self (follower_id != following_id)
    INDEX: idx_user_follows_follower (follower_id)
    INDEX: idx_user_follows_following (following_id)
    INDEX: idx_user_follows_created_at (created_at)

## 11. Notes

### 11.1 Authentication

    Public Endpoints: Các endpoint đánh dấu là "Public" có thể truy cập mà không cần JWT token.
    
    Authenticated Endpoints: Các endpoint yêu cầu authentication phải gửi JWT token trong header:
    
        Authorization: Bearer <your_jwt_token>

### 11.2 JWT Token

    Token có thời hạn 10 giờ kể từ khi đăng nhập.
    
    Token chứa email của người dùng trong trường subject.
    
    Khi token hết hạn, cần đăng nhập lại để lấy token mới.

### 11.3 Cascade Delete

    Khi xóa recipe, tất cả ingredients, steps, step_images, recipe_likes, recipe_bookmarks, recipe_comments và recipe_ratings liên quan sẽ tự động bị xóa.
    
    Khi xóa step, tất cả step_images liên quan sẽ tự động bị xóa.
    
    Khi xóa user, tất cả recipe_likes, recipe_bookmarks, recipe_comments và recipe_ratings của user đó sẽ tự động bị xóa.
    
    Khi xóa user, tất cả search_history của user đó sẽ tự động bị xóa (CASCADE DELETE).
    
    Khi xóa comment, tất cả replies (comments con) sẽ tự động bị xóa (cascade delete).
    
    Khi xóa user, tất cả notifications liên quan (cả nhận và gửi) sẽ tự động bị xóa (CASCADE DELETE).
    
    Khi xóa recipe hoặc comment, tất cả notifications liên quan sẽ tự động bị xóa (CASCADE DELETE).

### 11.4 Data Relationships

    1 User có nhiều Recipes (One-to-Many)
    
    1 User có nhiều Recipe Likes (One-to-Many)
    
    1 User có nhiều Recipe Bookmarks (One-to-Many)
    
    1 User có nhiều Recipe Comments (One-to-Many)
    
    1 User có nhiều Recipe Ratings (One-to-Many)
    
    1 User có nhiều Search History entries (One-to-Many)
    
    1 User nhận nhiều Notifications (One-to-Many)
    
    1 User (actor) tạo nhiều Notifications (One-to-Many)
    
    1 Recipe có nhiều Ingredients (One-to-Many)
    
    1 Recipe có nhiều Steps (One-to-Many)
    
    1 Recipe có nhiều Likes (One-to-Many)
    
    1 Recipe có nhiều Bookmarks (One-to-Many)
    
    1 Recipe có nhiều Comments (One-to-Many)
    
    1 Recipe có nhiều Ratings (One-to-Many)
    
    1 Step có nhiều Images (One-to-Many)
    
    1 Comment có nhiều Replies/Comments con (One-to-Many, self-referencing)
    
    1 Recipe có nhiều Notifications (One-to-Many)
    
    1 Comment có nhiều Notifications (One-to-Many)

### 11.5 Recipe Response Fields

    likesCount: Tổng số lượt like của công thức.
    
    isLikedByCurrentUser: true nếu người dùng hiện tại đã like công thức, false nếu chưa like hoặc chưa đăng nhập.
    
    bookmarksCount: Tổng số lượt bookmark/lưu của công thức.
    
    isBookmarkedByCurrentUser: true nếu người dùng hiện tại đã bookmark công thức, false nếu chưa bookmark hoặc chưa đăng nhập.
    
    averageRating: Đánh giá trung bình của công thức (0.00 - 5.00).
    
    ratingsCount: Tổng số lượt đánh giá.
    
    userRating: Đánh giá của người dùng hiện tại (1-5 hoặc null nếu chưa đánh giá hoặc chưa đăng nhập).
    
    commentsCount: Tổng số bình luận (không tính replies).
    
    Các endpoint public (không cần authentication) vẫn trả về thông tin like, bookmark và rating, nhưng isLikedByCurrentUser, isBookmarkedByCurrentUser và userRating sẽ luôn là false/null.

### 11.6 Rating System

    Rating phải từ 1 đến 5 sao.
    
    Mỗi người dùng chỉ có thể đánh giá một công thức một lần (unique constraint).
    
    Khi cập nhật rating, average_rating và ratings_count của recipe sẽ tự động được cập nhật.
    
    Rating distribution cho biết số lượng đánh giá cho mỗi mức sao (1-5).

### 11.7 Comment System

    Comments hỗ trợ nested replies (bình luận có thể trả lời bình luận khác).
    
    parentCommentId = null: Bình luận gốc (root comment).
    
    parentCommentId != null: Bình luận trả lời (reply).
    
    Khi xóa bình luận gốc, tất cả replies sẽ bị xóa theo (cascade delete).
    
    Chỉ người tạo bình luận mới có quyền sửa/xóa bình luận đó.

### 11.8 Notification System

    Thông báo tự động được tạo khi:
        - Có người like công thức của bạn
        - Có người comment công thức của bạn
        - Có người bookmark/lưu công thức của bạn
        - Có người rate công thức của bạn
        - Có người reply comment của bạn (tính năng này cần tích hợp thêm vào CommentService)
    
    Không tạo thông báo nếu:
        - User tự thực hiện hành động trên công thức/comment của chính mình
        - Đã có thông báo chưa đọc giống hệt (để tránh spam)
    
    Notification types:
        - LIKE: Thích công thức
        - COMMENT: Bình luận công thức
        - RATING: Đánh giá công thức
        - REPLY: Trả lời bình luận
        - BOOKMARK: Lưu công thức
    
    Thông báo bao gồm thông tin:
        - Actor (người thực hiện): name, avatar
        - Recipe (công thức liên quan): title, image
        - Message: Nội dung mô tả thông báo
        - isRead: Trạng thái đã đọc/chưa đọc
        - createdAt: Thời gian tạo
    
    API cho phép:
        - Lấy tất cả thông báo
        - Lấy chỉ thông báo chưa đọc
        - Đếm số thông báo chưa đọc
        - Đánh dấu đã đọc (1 hoặc tất cả)
        - Xóa thông báo (1 hoặc tất cả)

### 11.9 Search History System

    Lịch sử tìm kiếm tự động được lưu khi user gọi API /api/recipes/search?title=xxx
    
    Chỉ lưu lịch sử nếu user đã đăng nhập (có JWT token).
    
    Mỗi lần search sẽ tạo một entry mới (để track tần suất search).
    
    API /api/search-history mặc định trả về các query duy nhất (distinct), giúp hiển thị gợi ý search.
    
    Dùng showAll=true để xem toàn bộ lịch sử bao gồm cả entries trùng lặp.
    
    User có thể xóa toàn bộ lịch sử hoặc xóa từng query cụ thể.
    
    Khi xóa user, toàn bộ lịch sử tìm kiếm của user đó sẽ tự động bị xóa (CASCADE DELETE).

### 11.10 User Follow System

    Người dùng có thể follow và unfollow người dùng khác.
    
    Không thể tự follow chính mình (CHECK constraint).
    
    Một user chỉ có thể follow người khác một lần duy nhất (UNIQUE constraint trên follower_id + following_id).
    
    Khi follow một người, hệ thống tự động tạo notification cho người được follow.
    
    Following Feed (/api/recipes/following-feed) trả về các công thức từ những người mà user đang follow, sắp xếp theo thời gian mới nhất.
    
    Số lượng followers/following được tính động thông qua các service methods.
    
    Khi xóa user, tất cả follow relationships liên quan sẽ tự động bị xóa (CASCADE DELETE).