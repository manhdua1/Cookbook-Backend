# Cookbook Backend

Backend service for a cookbook and recipe sharing application. The project contains a Spring Boot REST API and a Python AI service that uses RAG to answer recipe-related questions from a local recipe dataset.

## Table of Contents

- [Tech Stack](#tech-stack)
- [Main Features](#main-features)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Configuration](#configuration)
- [Running the Project](#running-the-project)
- [Main APIs](#main-apis)
- [Request Examples](#request-examples)
- [Testing and Build](#testing-and-build)
- [Operational Notes](#operational-notes)

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- Spring Web, WebFlux
- Spring Data JPA
- Spring Security, JWT
- Spring Validation
- Spring Mail
- MySQL
- Lombok
- Springdoc OpenAPI / Swagger UI
- Python FastAPI for the AI service
- LangChain, ChromaDB, Ollama for RAG

## Main Features

- Register, log in, send OTP by email, reset password, and change password.
- Manage users and user profiles.
- Create, update, delete, search, and filter recipes.
- Manage recipe ingredients, cooking steps, and step images.
- Like, bookmark, rate, and comment on recipes.
- Follow users, view followers/following, and load a following feed.
- Store search history, recently viewed recipes, and trending search keywords.
- Upload one or multiple images to the server.
- Manage user notifications.
- Admin APIs for creating, updating, deleting, and bulk importing recipes.
- AI chat for recipe Q&A through the Python AI service.

## Project Structure

```text
.
|-- ai-service/                         # FastAPI service for the RAG chatbot
|   |-- dataset.json                    # Recipe dataset for AI retrieval
|   |-- load_recipes.py                 # Loads the dataset into Chroma vector DB
|   |-- rag_recipes.py                  # AI service /chat API
|   `-- recipes_db/                     # Generated Chroma vector DB
|-- src/main/java/com/dao/cookbook/
|   |-- config/                         # Security, JWT, static upload config
|   |-- controller/                     # REST controllers
|   |-- dto/                            # Request/response DTOs
|   |-- entity/                         # JPA entities
|   |-- mapper/                         # Entity <-> DTO mapping
|   |-- repository/                     # Spring Data repositories
|   `-- service/                        # Business logic
|-- src/main/resources/application.yaml # Application configuration
|-- pom.xml
|-- mvnw
`-- mvnw.cmd
```

The repository also contains a copied AI service under `src/main/ai-service-python/`. For manual local runs, use the root-level `ai-service/` directory.

## Requirements

- JDK 21
- MySQL 8.x or compatible
- Maven Wrapper, already included in the repository
- Python 3.10+ if running the AI service
- Ollama if using the AI chat feature

## Configuration

Main configuration file:

```text
src/main/resources/application.yaml
```

Current configuration shape:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cookbook
    username: root
    password: root

  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password

jwt:
  secret: your_secret_key_your_secret_key_your_secret_key
  expiration: 36000000

file:
  upload-dir: uploads

app:
  base-url: http://localhost:8080

ai:
  service:
    url: http://localhost:8001
```

Important values to review before running:

| Key | Description |
| --- | --- |
| `spring.datasource.url` | MySQL database URL |
| `spring.datasource.username` | MySQL username |
| `spring.datasource.password` | MySQL password |
| `spring.mail.username` | Email account used to send OTP |
| `spring.mail.password` | SMTP app password |
| `jwt.secret` | Secret used to sign JWT tokens |
| `file.upload-dir` | Directory used to store uploaded files |
| `ai.service.url` | Python AI service URL |

Create the local database:

```sql
CREATE DATABASE cookbook CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

The project currently does not include SQL migrations or Flyway/Liquibase setup. If your local database has no schema yet, you can enable Hibernate auto update for development:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

Do not use `ddl-auto: update` as a replacement for real migrations in production.

## Running the Project

### 1. Start MySQL

Make sure MySQL is running and the `cookbook` database exists.

### 2. Run the Spring Boot backend

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux/macOS:

```bash
./mvnw spring-boot:run
```

The backend runs at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

### 3. Run the AI service

Run this only if you need `POST /api/ai/chat`.

```powershell
cd ai-service
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install fastapi uvicorn langchain-community langchain-core chromadb ollama
```

Install the required Ollama models:

```powershell
ollama pull mxbai-embed-large
ollama pull llama3.2:3b
```

If the vector database does not exist or you want to reload the dataset:

```powershell
python load_recipes.py
```

Run the AI service on port `8001`:

```powershell
uvicorn rag_recipes:app --host 0.0.0.0 --port 8001
```

Test the AI service directly:

```powershell
curl -X POST http://localhost:8001/chat `
  -H "Content-Type: application/json" `
  -d "{\"question\":\"How do I cook beef pho?\"}"
```

## Main APIs

Base URL:

```text
http://localhost:8080
```

### Authentication

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/auth/send-otp?email={email}` | Send registration OTP |
| `POST` | `/api/auth/register` | Register an account using OTP |
| `POST` | `/api/auth/login?username={email}&password={password}` | Log in and return a JWT |
| `POST` | `/api/auth/forgot-password` | Send password reset OTP |
| `POST` | `/api/auth/reset-password` | Reset password |
| `POST` | `/api/auth/change-password` | Change password |

After login, send the JWT in this header:

```text
Authorization: Bearer <JWT_TOKEN>
```

`SecurityConfig` currently uses `permitAll()` to make API testing easier. However, APIs that need the current user still require a valid JWT header so the backend can resolve the user from the security context.

### User

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/users` | Get all users |
| `GET` | `/api/users/{id}` | Get user details |
| `POST` | `/api/users` | Create user |
| `PUT` | `/api/users/{id}` | Update user profile |
| `DELETE` | `/api/users/{id}` | Delete user |
| `GET` | `/api/users/exists?email={email}` | Check whether an email exists |
| `GET` | `/api/users/me` | Get the current user |

### Follow

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/users/{userId}/follow` | Follow a user |
| `DELETE` | `/api/users/{userId}/follow` | Unfollow a user |
| `GET` | `/api/users/{userId}/following` | Get users followed by this user |
| `GET` | `/api/users/{userId}/followers` | Get this user's followers |
| `GET` | `/api/users/{userId}/is-following` | Check follow status |
| `GET` | `/api/users/{userId}/follow-stats` | Get follower/following counts |

### Recipe

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/recipes/getRecipes` | Get all recipes |
| `GET` | `/api/recipes/{id}` | Get recipe details |
| `GET` | `/api/recipes/user/{userId}` | Get recipes by user |
| `GET` | `/api/recipes/my-recipes` | Get recipes of the current user |
| `GET` | `/api/recipes/search?title={title}` | Search recipes by title |
| `POST` | `/api/recipes/filter-by-ingredients` | Filter recipes by included/excluded ingredients |
| `POST` | `/api/recipes` | Create recipe |
| `PUT` | `/api/recipes/{id}` | Update recipe |
| `DELETE` | `/api/recipes/{id}` | Delete recipe |
| `GET` | `/api/recipes/following-feed` | Get recipe feed from followed users |
| `GET` | `/api/recipes/recently-viewed` | Get recently viewed recipes |
| `DELETE` | `/api/recipes/recently-viewed` | Clear all view history |
| `DELETE` | `/api/recipes/recently-viewed/{recipeId}` | Remove one recipe from view history |

### Likes and Bookmarks

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/recipes/{id}/like` | Like a recipe |
| `DELETE` | `/api/recipes/{id}/like` | Unlike a recipe |
| `POST` | `/api/recipes/{id}/toggle-like` | Toggle like status |
| `GET` | `/api/recipes/{id}/is-liked` | Check whether the recipe is liked |
| `GET` | `/api/recipes/liked` | Get IDs of liked recipes |
| `POST` | `/api/recipes/{id}/bookmark` | Bookmark a recipe |
| `DELETE` | `/api/recipes/{id}/bookmark` | Remove bookmark |
| `POST` | `/api/recipes/{id}/toggle-bookmark` | Toggle bookmark status |
| `GET` | `/api/recipes/{id}/is-bookmarked` | Check whether the recipe is bookmarked |
| `GET` | `/api/recipes/bookmarked` | Get IDs of bookmarked recipes |

### Comments and Ratings

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/recipes/{recipeId}/comments` | Get recipe comments |
| `POST` | `/api/recipes/{recipeId}/comments` | Add a comment or reply |
| `PUT` | `/api/recipes/{recipeId}/comments/{commentId}` | Update a comment |
| `DELETE` | `/api/recipes/{recipeId}/comments/{commentId}` | Delete a comment |
| `POST` | `/api/recipes/{recipeId}/ratings` | Add or update a rating |
| `GET` | `/api/recipes/{recipeId}/ratings` | Get recipe ratings |
| `GET` | `/api/recipes/{recipeId}/ratings/my-rating` | Get the current user's rating |
| `GET` | `/api/recipes/{recipeId}/ratings/stats` | Get rating statistics |
| `DELETE` | `/api/recipes/{recipeId}/ratings` | Delete the current user's rating |

### Upload

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/upload/image` | Upload one image |
| `POST` | `/api/upload/images` | Upload multiple images |

Uploaded files are stored in `uploads/` and served from:

```text
http://localhost:8080/uploads/{fileName}
```

The `type` parameter supports these upload groups: `avatars`, `recipes`, `steps`, `general`.

### Search History

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/search-history?limit=20&showAll=false` | Get search history |
| `POST` | `/api/search-history` | Save a search query |
| `DELETE` | `/api/search-history` | Clear all search history |
| `DELETE` | `/api/search-history/query?query={keyword}` | Delete one search query |
| `GET` | `/api/search-history/stats` | Get search history statistics |
| `GET` | `/api/search-history/trending?limit=10&days=30` | Get trending keywords |

### Notifications

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/notifications` | Get all notifications |
| `GET` | `/api/notifications/unread` | Get unread notifications |
| `GET` | `/api/notifications/unread/count` | Count unread notifications |
| `PUT` | `/api/notifications/{id}/read` | Mark one notification as read |
| `PUT` | `/api/notifications/read-all` | Mark all notifications as read |
| `DELETE` | `/api/notifications/{id}` | Delete one notification |
| `DELETE` | `/api/notifications` | Delete all notifications |

### Admin

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/admin/recipes` | Create a recipe |
| `POST` | `/api/admin/recipes/bulk` | Create recipes in bulk |
| `PUT` | `/api/admin/recipes/{id}` | Update a recipe |
| `DELETE` | `/api/admin/recipes/{id}` | Delete a recipe |
| `POST` | `/api/recipes/admin/create` | Create a recipe for a specific user |

### AI

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/ai/chat` | Recipe Q&A through the AI service |

## Request Examples

### Register

Send OTP:

```powershell
curl -X POST "http://localhost:8080/api/auth/send-otp?email=user@example.com"
```

Register:

```json
{
  "fullName": "Nguyen Van A",
  "email": "user@example.com",
  "password": "123456",
  "otp": "123456"
}
```

Log in:

```powershell
curl -X POST "http://localhost:8080/api/auth/login?username=user@example.com&password=123456"
```

### Create a Recipe

```json
{
  "title": "Beef Pho",
  "imageUrl": "http://localhost:8080/uploads/recipes/beef-pho.jpg",
  "servings": 4,
  "cookingTime": 90,
  "ingredients": [
    {
      "name": "Beef",
      "quantity": "500",
      "unit": "g"
    },
    {
      "name": "Pho noodles",
      "quantity": "1",
      "unit": "kg"
    }
  ],
  "steps": [
    {
      "stepNumber": 1,
      "title": "Simmer the bones and prepare the broth",
      "images": []
    },
    {
      "stepNumber": 2,
      "title": "Blanch noodles, add beef, and pour in the broth",
      "images": []
    }
  ]
}
```

### Filter Recipes by Ingredients

```json
{
  "includeIngredients": ["beef", "onion"],
  "excludeIngredients": ["shrimp", "seafood"]
}
```

### AI Chat

```powershell
curl -X POST http://localhost:8080/api/ai/chat `
  -H "Content-Type: application/json" `
  -d "{\"question\":\"I have beef and pho noodles. What should I cook?\"}"
```

## Testing and Build

Run tests:

```powershell
.\mvnw.cmd test
```

Build package:

```powershell
.\mvnw.cmd clean package
```

Run the JAR after building:

```powershell
java -jar target/cookbook-0.0.1-SNAPSHOT.jar
```

## Operational Notes

- Do not commit real email passwords, database passwords, or JWT secrets to the repository.
- Prefer environment variables or dedicated profiles for development, staging, and production.
- The AI service depends on Ollama and the `mxbai-embed-large` and `llama3.2:3b` models.
- If `/api/ai/chat` is called while the AI service is not running, the backend will return a server error.
- Image uploads are limited to 5MB in the controller and 10MB in multipart configuration.
- Uploaded files are publicly served through `/uploads/**`.
