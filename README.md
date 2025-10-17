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

## 3. Recipe API

Endpoint quản lý các công thức nấu ăn.

Controller: RecipeController
Base Path: /api/recipes

### 3.1 Lấy danh sách tất cả công thức

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
                        "description": "Rửa sạch xương bò, thịt bò",
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
                        "description": "Cho xương bò vào nồi nước sôi, ninh trong 3 tiếng",
                        "images": []
                    }
                ],
                "createdAt": "2025-10-15T10:30:00",
                "updatedAt": "2025-10-15T10:30:00"
            }
        ]

### 3.2 Lấy thông tin công thức theo ID

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

### 3.3 Lấy công thức theo User ID

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

### 3.4 Lấy công thức của tôi

    Method: GET

    Endpoint: /api/recipes/my-recipes

    Mô tả: Trả về danh sách tất cả công thức của người dùng hiện tại (dựa trên JWT token). (Requires Authentication)

    Headers:

        Authorization: Bearer <JWT_TOKEN>

    Responses:

        200 OK: Trả về mảng các công thức của người dùng hiện tại.

        401 Unauthorized: Người dùng chưa đăng nhập hoặc token không hợp lệ.

### 3.5 Tìm kiếm công thức theo tiêu đề

    Method: GET

    Endpoint: /api/recipes/search

    Mô tả: Tìm kiếm công thức theo từ khóa trong tiêu đề (không phân biệt chữ hoa/thường). (Public - không cần xác thực)

    Query Parameters:

        title (String): Từ khóa cần tìm kiếm trong tiêu đề công thức.

    Example: /api/recipes/search?title=phở

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

### 3.6 Tạo công thức mới

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
                "description": "Đập trứng, thái nhỏ hành lá",
                "images": []
            },
            {
                "stepNumber": 2,
                "title": "Chiên",
                "description": "Đun nóng chảo, cho trứng và cơm vào chiên",
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

### 3.7 Cập nhật công thức

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

### 3.8 Xóa công thức

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

## 4. Database Schema

### 4.1 Bảng recipes

    id: BIGINT (Primary Key, Auto Increment)
    title: VARCHAR(255) NOT NULL
    image_url: VARCHAR(500)
    servings: INT NOT NULL
    cooking_time: INT
    user_id: BIGINT NOT NULL (Foreign Key -> users.id)
    created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### 4.2 Bảng ingredients

    id: BIGINT (Primary Key, Auto Increment)
    recipe_id: BIGINT NOT NULL (Foreign Key -> recipes.id)
    name: VARCHAR(255) NOT NULL
    quantity: VARCHAR(50)
    unit: VARCHAR(50)

### 4.3 Bảng recipe_steps

    id: BIGINT (Primary Key, Auto Increment)
    recipe_id: BIGINT NOT NULL (Foreign Key -> recipes.id)
    step_number: INT NOT NULL
    title: VARCHAR(255) NOT NULL
    description: TEXT

### 4.4 Bảng step_images

    id: BIGINT (Primary Key, Auto Increment)
    step_id: BIGINT NOT NULL (Foreign Key -> recipe_steps.id)
    image_url: VARCHAR(500) NOT NULL
    order_number: INT

## 5. Notes

### 5.1 Authentication

    Public Endpoints: Các endpoint đánh dấu là "Public" có thể truy cập mà không cần JWT token.
    
    Authenticated Endpoints: Các endpoint yêu cầu authentication phải gửi JWT token trong header:
    
        Authorization: Bearer <your_jwt_token>

### 5.2 JWT Token

    Token có thời hạn 10 giờ kể từ khi đăng nhập.
    
    Token chứa email của người dùng trong trường subject.
    
    Khi token hết hạn, cần đăng nhập lại để lấy token mới.

### 5.3 Cascade Delete

    Khi xóa recipe, tất cả ingredients, steps và step_images liên quan sẽ tự động bị xóa.
    
    Khi xóa step, tất cả step_images liên quan sẽ tự động bị xóa.

### 5.4 Data Relationships

    1 User có nhiều Recipes (One-to-Many)
    
    1 Recipe có nhiều Ingredients (One-to-Many)
    
    1 Recipe có nhiều Steps (One-to-Many)
    
    1 Step có nhiều Images (One-to-Many)