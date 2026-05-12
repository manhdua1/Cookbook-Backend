from fastapi import FastAPI, Body
from langchain_community.vectorstores import Chroma
from langchain_community.llms import Ollama
from langchain_community.embeddings import OllamaEmbeddings

app = FastAPI()

# Load vector database
embeddings = OllamaEmbeddings(model="mxbai-embed-large")
db = Chroma(persist_directory="./recipes_db", embedding_function=embeddings)
# Tăng k=5 để lấy nhiều kết quả hơn, tăng khả năng tìm đúng món
retriever = db.as_retriever(search_kwargs={"k": 5})

# Dùng model nhỏ để tăng tốc - llama3.2:3b nhanh hơn 3-4x so với 7b
llm = Ollama(model="llama3.2:3b", num_gpu=1)

@app.post("/chat")
def chat(request: dict = Body(...)):
    question = request.get("question")
    
    # Hybrid search: Thử tìm bằng keyword trước (extract tên món)
    keywords = ["phở bò", "phở gà", "cơm chiên", "bánh mì", "bún chả", "gà rán"]
    keyword_docs = []
    for keyword in keywords:
        if keyword.lower() in question.lower():
            keyword_docs = db.similarity_search(keyword, k=3)
            break
    
    # Nếu tìm được bằng keyword thì ưu tiên, không thì dùng semantic search
    if keyword_docs:
        docs = keyword_docs
    else:
        docs = retriever.invoke(question)
    
    # Tạo context - CHỈ lấy món đầu tiên (chính xác nhất)
    if docs:
        context = docs[0].page_content
    else:
        context = "Không tìm thấy món phù hợp."
    
    # Tạo prompt đơn giản, tránh model bị confuse
    prompt = f"""Dựa vào công thức sau, trả lời đầy đủ. KHÔNG thêm thông tin khác.

Công thức:
{context}

Câu hỏi: {question}

Trả lời:"""
    
    # Gọi LLM - tăng num_predict để đủ nội dung
    answer = llm.invoke(prompt, num_predict=500)
    
    return {
        "answer": answer,
        "sources": [{"title": doc.metadata.get("title"), "id": doc.metadata.get("id")} for doc in docs]
    }
