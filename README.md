# GigTasker Chat Service 💬

The **Chat Service** enables secure, real-time communication between task posters and workers. It leverages a hybrid architecture using **WebSockets (STOMP)** for instant message delivery and **MongoDB** for high-performance message persistence.

## 🚀 Features

* **Real-Time Messaging:** Instant 1-on-1 chat using WebSockets over STOMP protocol.
* **Persistent History:** Stores all conversations and messages in MongoDB for retrieval anytime.
* **Contextual Rooms:** Chats are linked to specific Tasks (`taskId`), ensuring conversations are organized by context.
* **Secure Handshake:** Validates JWT tokens during the WebSocket handshake via a custom Interceptor.
* **Inbox Management:** APIs to fetch "My Conversations" and load full message history.
* **Consistent Room IDs:** Uses a sorted UUID algorithm (`task_101_A_B`) to ensure a single source of truth for every conversation pair.

---

## 🛠️ Tech Stack

* **Java:** 25 (Amazon Corretto)
* **Framework:** Spring Boot 4.0.0
* **Database:** MongoDB 7.0 (NoSQL)
* **Protocol:** WebSocket / STOMP / SockJS
* **Security:** Spring Security 6 (OAuth2 Resource Server)
* **Discovery:** Netflix Eureka Client
* **Documentation:** SpringDoc OpenAPI (Swagger)

---

## 📂 Data Model (MongoDB)

### ChatRoom Collection (`chat_rooms`)
Links two users in the context of a task.
```json
{
  "_id": "task_1_uuidA_uuidB",
  "taskId": 1,
  "chatId": "task_1_uuidA_uuidB",
  "senderId": "uuid-A",
  "recipientId": "uuid-B"
}
```
### ChatMessage Collection (`chat_messages`)

Stores the actual conversation log.

JSON

```
{
  "_id": "msg_uuid",
  "chatId": "task_1_uuidA_uuidB",
  "taskId": 1,
  "senderId": "uuid-A",
  "recipientId": "uuid-B",
  "content": "Hello, I can do this job!",
  "timestamp": "2025-11-30T10:00:00.000Z"
}

```

* * * * *

📦 Installation & Run
---------------------

### 1\. Start MongoDB

Ensure the MongoDB container is running via the root `docker-compose.yml`.

Bash

```
docker-compose up -d mongo

```

### 2\. Build the Service

Bash

```
mvn clean package -DskipTests

```

### 3\. Run via Docker

Bash

```
cd ../../gigtasker-config
docker-compose up -d --build chat-service

```

* * * * *

🔌 API Documentation
--------------------

Once running, the API documentation is available via the Centralized Gateway Portal:

-   **Swagger UI:** [http://localhost:9090/swagger-ui.html](https://www.google.com/search?q=http://localhost:9090/swagger-ui.html)

    -   *Select "Chat Service" from the dropdown.*

### REST Endpoints (HTTP)

| **Method** | **Endpoint** | **Description** |
| --- | --- | --- |
| `GET` | `/api/v1/chat/rooms` | Get list of active conversations for current user |
| `GET` | `/api/v1/chat/messages/{taskId}/{userId}` | Get message history with a specific user for a task |

### WebSocket Endpoints (WS)

-   **Handshake URL:** `ws://localhost:9090/ws-chat` (Rewritten to `/ws` by Gateway)

-   **Send Destination:** `/app/chat`

-   **Subscribe Destination:** `/user/queue/messages`

* * * * *

🔐 Security & Handshake
-----------------------

Since standard WebSockets do not support HTTP Headers during the session, authentication is handled during the **CONNECT** frame of the STOMP protocol.

1.  **Client:** Sends `Authorization: Bearer <token>` in the STOMP CONNECT headers.

2.  **Interceptor:** `WebSocketConfig` extracts the token, validates it via `JwtDecoder`, and sets the User Principal (UUID).

3.  **Authorization:** `WebSocketSecurityConfig` ensures only authenticated sessions can send messages to `/app/**`.

* * * * *

🤝 Contributing
---------------

-   **MongoDB Config:** The service uses `MongoClientSettingsBuilderCustomizer` to force `UuidRepresentation.STANDARD` to handle Java UUIDs correctly. Do not remove this configuration bean.

-   **Room Logic:** When modifying room creation logic, ensure you maintain the sorting of UUIDs (`sender.compareTo(recipient)`) to prevent duplicate rooms (A-B vs B-A).
