# Hướng dẫn sử dụng AI Chat API

## Kiến trúc

```
Client/Mobile App → Spring Boot (8080) → Python AI Service (8001) → Ollama LLM
                    [Expose qua ngrok]    [Local only]
```

## Bước 1: Chạy Python AI Service (Local)

```bash
cd /home/manhdua/Cookbook-Backend/src/main/ai-service-python
venv/bin/python -m uvicorn rag_recipes:app --port 8001
```

Python AI service chạy tại: `http://localhost:8001` (không cần ngrok cho service này)

## Bước 2: Chạy Spring Boot

```bash
cd /home/manhdua/Cookbook-Backend
./mvnw spring-boot:run
```

Spring Boot chạy tại: `http://localhost:8080`

## Bước 3: Test API Local

```bash
curl -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Cách làm phở bò?"}'
```

Response:
```json
{
  "answer": "Cách làm phở bò:\n\nBước 1: ...",
  "sources": [
    {"title": "Phở bò", "id": 2927}
  ]
}
```

## Bước 4: Expose Spring Boot qua Ngrok

### Cài đặt ngrok (nếu chưa có):
```bash
# Arch Linux
sudo pacman -S ngrok

# Hoặc download từ: https://ngrok.com/download
```

### Chạy ngrok:
```bash
ngrok http 8080
```

Output:
```
Forwarding   https://abc123.ngrok-free.app -> http://localhost:8080
```

## Bước 5: Test từ Mobile/External

```bash
# Thay abc123.ngrok-free.app bằng URL ngrok của bạn
curl -X POST https://abc123.ngrok-free.app/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Cách làm phở bò?"}'
```

## API Documentation

### Endpoint: POST /api/ai/chat

**Request:**
```json
{
  "question": "string" // Câu hỏi về công thức nấu ăn
}
```

**Response:**
```json
{
  "answer": "string",  // Câu trả lời chi tiết từ AI
  "sources": [         // Danh sách công thức tham khảo
    {
      "title": "string",
      "id": number
    }
  ]
}
```

**Example:**
```bash
# Request
POST /api/ai/chat
{
  "question": "Cách làm gà rán giòn?"
}

# Response
{
  "answer": "Để làm gà rán giòn:\n\nBước 1: Ướp gà với...",
  "sources": [
    {"title": "Gà rán KFC", "id": 1234}
  ]
}
```

## Lưu ý quan trọng

### 1. Python AI Service phải chạy trước
- Python AI (port 8001) phải chạy **trước** khi start Spring Boot
- Nếu Python AI chết → Spring Boot API trả lỗi 500

### 2. Ngrok chỉ cần cho Spring Boot
- ✅ Spring Boot (8080): Expose qua ngrok → Client truy cập
- ❌ Python AI (8001): Chỉ chạy local, Spring Boot gọi internal

### 3. Ngrok Free Plan Limitations
- Session timeout sau **2 giờ** → phải restart
- URL thay đổi mỗi lần restart
- Rate limit: 40 requests/minute

### 4. Response Time
- AI mất **15-20 giây** để trả lời (bình thường)
- Client app nên show loading indicator

## Troubleshooting

### Lỗi: "Connection refused" từ Spring Boot
```
Lỗi khi gọi AI service: Connection refused
```

**Giải pháp:**
- Check Python AI đang chạy: `curl http://localhost:8001`
- Nếu chưa chạy: Start lại Python AI service

### Lỗi: Ngrok "502 Bad Gateway"
```
502 Bad Gateway
```

**Giải pháp:**
- Spring Boot chưa chạy hoặc đã crash
- Restart Spring Boot: `./mvnw spring-boot:run`

### Lỗi: "CORS" khi gọi từ Browser
```
Access to XMLHttpRequest blocked by CORS policy
```

**Giải pháp:**
- AIController đã có `@CrossOrigin(origins = "*")`
- Nếu vẫn lỗi, check SecurityConfig

### Response quá chậm (> 60s)
```
Request timeout
```

**Giải pháp:**
- Check Python AI logs
- Model có thể bị overload
- Restart Python AI service

## Production Setup

### Option 1: Ngrok Pro (Recommended)
```bash
# Custom domain (URL cố định)
ngrok http 8080 --domain=your-app.ngrok.app
```

### Option 2: Deploy lên VPS
```bash
# Deploy cả Spring Boot + Python AI lên server
# Dùng nginx reverse proxy thay ngrok
```

## Cấu hình nâng cao

### Thay đổi AI Service URL (nếu cần)

**application.yaml:**
```yaml
ai:
  service:
    url: http://localhost:8001  # Default
    # url: http://another-host:8001  # Custom
```

### Thêm Authentication cho AI endpoint

**AIController.java:**
```java
@PostMapping("/chat")
public ResponseEntity<AIChatResponseDTO> chat(
    @RequestHeader("Authorization") String token,
    @RequestBody AIChatRequestDTO request
) {
    // Validate token
    if (!isValidToken(token)) {
        return ResponseEntity.status(401).build();
    }
    
    // Process request...
}
```

## Commands Cheat Sheet

```bash
# 1. Start Python AI
cd src/main/ai-service-python
venv/bin/python -m uvicorn rag_recipes:app --port 8001

# 2. Start Spring Boot (terminal mới)
cd /home/manhdua/Cookbook-Backend
./mvnw spring-boot:run

# 3. Start ngrok (terminal mới)
ngrok http 8080

# 4. Test local
curl -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"question":"Cách làm phở bò?"}'

# 5. Test ngrok (thay YOUR_NGROK_URL)
curl -X POST https://YOUR_NGROK_URL/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"question":"Cách làm phở bò?"}'
```
