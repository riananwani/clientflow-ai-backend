# ClientFlow AI 🤖

> A secure, role-based project-management backend with AI-assisted project summaries, structured task extraction, and document-grounded Q&A.

Built with **Java 21**, **Spring Boot 4**, **PostgreSQL**, **Spring Security**, **JWT**, **Docker**, and **Groq's OpenAI-compatible LLM API**.

---

## 🌐 Live Demo

- **API Base URL:** https://clientflow-ai-backend.onrender.com
- **Swagger UI:** https://clientflow-ai-backend.onrender.com/swagger-ui/index.html

> Note: Free tier — first request may take 50 seconds to wake up.

---

## Features

- **JWT Authentication** — Secure login and registration with role-based access (ADMIN, DEVELOPER, CLIENT)
- **Project Management** — Full CRUD for projects and tasks with status and priority tracking
- **Knowledge Base** — Upload project documents for AI-powered search
- **AI Project Summarizer** — Get instant AI-generated project status summaries
- **AI Task Extraction** — Paste meeting notes and watch tasks get created automatically
- **Project Document Q&A** — Ask questions about your project and get answers grounded in your uploaded documents, with source citations
- **Interactive API Docs** — Full Swagger UI documentation

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.x |
| Database | PostgreSQL |
| ORM | Spring Data JPA + Hibernate |
| Security | Spring Security + JWT + BCrypt |
| AI Provider | Groq |
| AI Model | openai/gpt-oss-20b |
| Build Tool | Maven |
| Containers | Docker + docker-compose |
| API Docs | Swagger / OpenAPI |

---

## Architecture

```text
Client / Swagger UI
       |
       v
Spring Boot REST API
       |
       ├── Spring Security + JWT Filter
       ├── User / Project / Task Modules
       ├── Knowledge Base Module
       └── AI Module
             └── Groq API (OpenAI-compatible endpoint)
       |
       v
PostgreSQL
```

---

## AI Design

ClientFlow AI uses Groq's OpenAI-compatible API with `openai/gpt-oss-20b` for:
- Project status summarization
- Structured task extraction from meeting notes
- Project document question answering

### AI Safety Controls
- API keys stored only in environment variables — never committed to Git
- All AI endpoints require JWT authentication
- AI-generated tasks are validated before being persisted to the database
- AI errors handled gracefully without exposing secrets
- Answers include source document citations

---

## API Endpoints

### Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Login and get JWT token |

### Users
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/users` | Get all users |
| POST | `/api/v1/users` | Create user |
| GET | `/api/v1/users/{id}` | Get user by ID |
| PUT | `/api/v1/users/{id}` | Update user |
| DELETE | `/api/v1/users/{id}` | Delete user |

### Projects
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/projects` | Get all projects |
| POST | `/api/v1/projects` | Create project |
| GET | `/api/v1/projects/{id}` | Get project by ID |
| PUT | `/api/v1/projects/{id}` | Update project |
| DELETE | `/api/v1/projects/{id}` | Delete project |

### Tasks
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/tasks` | Get all tasks |
| POST | `/api/v1/tasks` | Create task |
| GET | `/api/v1/tasks/{id}` | Get task by ID |
| GET | `/api/v1/tasks/project/{id}` | Get tasks by project |
| PUT | `/api/v1/tasks/{id}` | Update task |
| DELETE | `/api/v1/tasks/{id}` | Delete task |

### Knowledge Base
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/projects/{id}/documents` | Upload document |
| GET | `/api/v1/projects/{id}/documents` | Get all documents |
| GET | `/api/v1/projects/{id}/documents/{docId}` | Get document by ID |
| DELETE | `/api/v1/projects/{id}/documents/{docId}` | Delete document |

### AI Features
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/projects/{id}/ai/summarize` | AI project summary |
| POST | `/api/v1/projects/{id}/ai/extract-tasks` | Extract tasks from meeting notes |
| POST | `/api/v1/projects/{id}/ai/ask` | Ask questions about project documents |

---

## Running Locally

### Prerequisites
- Java 21
- PostgreSQL
- Maven

### Setup

1. Clone the repository
```bash
git clone https://github.com/riananwani/clientflow-ai-backend.git
cd clientflow-ai-backend
```

2. Create PostgreSQL database
```sql
CREATE DATABASE clientflow;
```

3. Create `src/main/resources/application-local.yml`:
```yaml
openai:
  api-key: your-groq-api-key-here
```

4. Run the application
```bash
./mvnw spring-boot:run
```

5. Access Swagger UI at `http://localhost:8080/swagger-ui/index.html`

### Running with Docker
```bash
docker-compose up --build
```

---

## Testing

```bash
./mvnw test
```

13 unit tests covering authentication and user-service logic.

---

## Authentication

All endpoints except `/api/v1/auth/**`, `/api/v1/health`, and Swagger/OpenAPI endpoints require a valid JWT.

After logging in, include the token in every protected request:

```http
Authorization: Bearer <your-jwt-token>
```

Example:
```bash
curl -H "Authorization: Bearer <your-jwt-token>" \
  http://localhost:8080/api/v1/projects
```

---

## Project Structure

src/main/java/clientflow/
├── auth/ ← JWT auth, Spring Security
├── user/ ← User management
├── project/ ← Project management
├── task/ ← Task management
├── knowledge/ ← Document upload
├── ai/ ← AI features (summarizer, task extraction, Q&A)
└── common/ ← Health endpoint, global error handler


---

## Roadmap

- [x] JWT authentication and role-based access control
- [x] User, Project and Task CRUD APIs
- [x] Knowledge base document upload
- [x] AI project summarization
- [x] AI task extraction from meeting notes
- [x] Project document Q&A
- [x] Swagger / OpenAPI documentation
- [x] Docker + docker-compose setup
- [ ] pgvector embeddings + true RAG retrieval
- [ ] Project membership authorization enforcement
- [ ] GitHub Actions CI pipeline
- [ ] Rate limiting on AI endpoints
- [ ] Live deployment
