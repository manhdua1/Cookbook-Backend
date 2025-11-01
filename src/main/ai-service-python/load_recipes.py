from langchain_community.vectorstores import Chroma
from langchain_community.embeddings import OllamaEmbeddings
from langchain_core.documents import Document
import json

with open("dataset.json", "r", encoding="utf-8") as f:
    recipes = json.load(f)

docs = []
for r in recipes:
    title = r.get("title", "")
    ingredients = ", ".join([i["name"] for i in r.get("ingredients", [])])
    steps = " ".join([s["title"] for s in r.get("steps", [])])

    content = f"Tên món: {title}\nNguyên liệu: {ingredients}\nCác bước: {steps}"
    docs.append(Document(page_content=content, metadata={"id": r["id"], "title": title}))

embeddings = OllamaEmbeddings(model="mxbai-embed-large")  # fast local embedding model
vectorstore = Chroma.from_documents(docs, embeddings, persist_directory="./recipes_db")
vectorstore.persist()

print(f"✅ Đã nạp {len(docs)} món ăn vào Vector DB.")