# Sarvam Chatbot

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-DD0031?logo=angular&logoColor=white)](https://angular.dev/)
[![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)](https://www.java.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?logo=postgresql&logoColor=white)](https://www.postgresql.org/)

Sarvam Chatbot is a full-stack chat application with a Spring Boot backend and an Angular frontend. It stores conversations in PostgreSQL and sends prompts to Sarvam AI for responses.

## Features

- Chat with the assistant in a clean web UI
- Create, browse, and delete conversations
- Persist chat history in PostgreSQL
- Backed by the Sarvam AI chat API

## Project Structure

- `backend/` - Spring Boot API, persistence, and Sarvam AI integration
- `frontend/` - Angular app for the chat interface

## Requirements

- Node.js 18+ and npm
- Java 17
- Maven
- PostgreSQL
- A `SARVAM_API_KEY`

## Local Setup

### 1. Configure the database and API key

The backend reads these environment variables, with defaults shown in `backend/src/main/resources/application.properties`:

- `DB_HOST` - default `localhost`
- `DB_PORT` - default `5433`
- `DB_NAME` - default `sarvam_chatbot`
- `DB_USERNAME` - default `postgres`
- `DB_PASSWORD` - default `postgres`
- `SARVAM_API_KEY` - required for chat requests
- `FRONTEND_URL` - default `http://localhost:4200`

### 2. Start the backend

From `backend/`:

```bash
mvn spring-boot:run
```

The API runs on `http://localhost:8081` by default.

### 3. Start the frontend

From `frontend/`:

```bash
npm install
npm start
```

The Angular app runs on `http://localhost:4200` and calls the backend at `http://localhost:8081/api` in development.

## API Overview

- `POST /api/chat` - send a message and get an assistant reply
- `GET /api/conversations` - list saved conversations
- `GET /api/conversations/{id}` - load a conversation history
- `DELETE /api/conversations/{id}` - delete a conversation

## Production Notes

Live deployments:

- Frontend: `https://sarvam-chatbot-frontend.onrender.com`
- Backend: `https://sarvam-chatbot-backend.onrender.com`

The frontend production environment is configured to call the deployed backend at:

`https://sarvam-chatbot-backend.onrender.com/api`

## License

No license has been added yet.
