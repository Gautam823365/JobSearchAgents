# AI Job Search & Application Assistant — Phase 1

Phase 1 scope: a **simple chatbot REST API** with three pieces, wired to match the
target architecture (Angular → Spring Boot → Python/AI → later: agents + PostgreSQL).

```
Angular (chat UI, :4200)
   │  POST /api/chat
   ▼
Spring Boot (:8080)          <- orchestrator / future auth, routing, DB access
   │  POST /chat
   ▼
Python FastAPI (:8000)       <- LLM call today; Job/Resume/Application agents later
```

Why this split: Spring Boot stays the stable, typed "front door" API that Angular
talks to (and later: auth, PostgreSQL, business rules). Python stays where the AI
work happens, since that's where you'll add the LangChain/agent logic, embeddings,
and model calls in later phases — without ever changing the Angular contract.

## Run it (3 terminals)

**1. AI service (Python)**
```bash
cd ai-service
python -m venv venv && source venv/bin/activate   # Windows: venv\Scripts\activate
pip install -r requirements.txt
cp .env.example .env   # then paste your ANTHROPIC_API_KEY into .env
export $(cat .env | xargs)   # or use python-dotenv / your OS's env mechanism
uvicorn main:app --reload --port 8000
```
Check: `curl http://localhost:8000/health`

**2. Backend (Spring Boot)**
```bash
cd backend
mvn spring-boot:run
```
Check: `curl http://localhost:8080/api/health`

**3. Frontend (Angular)**
```bash
cd frontend
npm install
npm start
```
Open http://localhost:4200

## API contract

`POST /api/chat` (Spring Boot, called by Angular)
```json
{ "message": "Find me Java backend jobs in Noida", "history": [] }
```
→
```json
{ "reply": "..." }
```

Spring Boot forwards this 1:1 to `POST /chat` on the Python service, so the
history/message shape stays identical end-to-end.

## What's stubbed for later phases

- `history` is passed but not persisted anywhere yet — no PostgreSQL in Phase 1.
- The Python service has one endpoint (`/chat`). Job Agent, Resume Agent, and
  Application Agent become additional endpoints or an internal router inside
  `ai-service/` in later phases.
- No auth yet — CORS is locked to `http://localhost:4200` for now.
- No voice input/output yet — text only, per the diagram's "Voice / Text" step.

## Project layout

```
job-assistant/
├── ai-service/        Python FastAPI — LLM call
├── backend/            Spring Boot — REST API Angular talks to
└── frontend/            Angular — chat UI
```
