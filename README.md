# 🚀 ReqAI – Enterprise AI-Powered Requirement Decomposition Platform

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-17-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

**Cloud Infrastructure:**
<br>
![Vercel](https://img.shields.io/badge/Vercel-000000?style=for-the-badge&logo=vercel&logoColor=white)
![Render](https://img.shields.io/badge/Render-46E3B7?style=for-the-badge&logo=render&logoColor=white)
![Upstash](https://img.shields.io/badge/Upstash-00E9A3?style=for-the-badge&logo=redis&logoColor=white)
![Confluent](https://img.shields.io/badge/Confluent-FFFFFF?style=for-the-badge&logo=apache-kafka&logoColor=black)
![Oracle](https://img.shields.io/badge/Oracle_Cloud-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)

### Enterprise Full-Stack AI Platform for Automated Requirement Engineering

</div>

---

# 📖 Overview

**ReqAI** is an enterprise-level, highly scalable Full-Stack web application designed to simplify and accelerate the software requirement engineering process.

The platform leverages **Artificial Intelligence** to automatically analyze unstructured customer requirement documents and transform them into actionable, development-ready software artifacts.

Instead of manually reading lengthy requirement documents, project teams can instantly obtain:

- 📌 Business Requirements
- 💻 Developer Tasks
- ✅ Test Scenarios

Beyond traditional CRUD operations, ReqAI is engineered for **high-performance**, **fault tolerance**, and **enterprise scalability** using asynchronous processing, event-driven architecture, caching mechanisms, real-time streaming technologies, and a distributed multi-cloud architecture.

---

# 🎯 Project Objective

In Agile software development, product owners and analysts often receive long, unstructured requirement documents.

Manually converting these documents into a product backlog is:

- Time-consuming
- Error-prone
- Difficult to maintain
- Inefficient for large projects

ReqAI automates this process by analyzing natural language requirement documents (TXT) and generating a structured backlog consisting of:

- **Business Requirements**
  - Priority estimation
  - Complexity estimation

- **Developer Tasks**

- **Test Scenarios**
  - Action
  - Expected Result

This significantly reduces analysis time while increasing consistency and development productivity.

---

# 🏗️ Architecture

ReqAI follows modern enterprise software architecture principles to ensure reliability, scalability, and maintainability.

## Distributed Cloud Infrastructure & Observability

ReqAI adopts a truly modern, distributed multi-cloud strategy to optimize cost, performance, and reliability across specialized providers:

- **Frontend (Vercel):** The Angular SPA is hosted on Vercel for lightning-fast global edge delivery.
- **Backend (Render):** The Spring Boot application runs on Render, providing robust computational power.
- **Messaging (Confluent Cloud):** Apache Kafka event streaming is managed by Confluent for enterprise-grade message delivery.
- **Caching (Upstash):** Serverless Redis caching is powered by Upstash for ultra-low latency data retrieval.
- **Observability (Oracle Cloud):** A dedicated Oracle Cloud instance hosts our complete observability stack:
  - **Grafana**: Central dashboard for visualizing all telemetry data in one place.
  - **Prometheus**: Collects and stores vital system and application metrics, enabling proactive monitoring.
  - **Grafana Loki**: Aggregates and indexes logs from all services for lightning-fast troubleshooting.
  - **Grafana Tempo**: Provides distributed tracing to track requests as they flow across components and asynchronous processes (like Kafka events).

This best-of-breed cloud architecture ensures performance bottlenecks and errors are detected and resolved instantly while keeping infrastructure costs optimized.

---

## Event-Driven Architecture (Apache Kafka)

Heavy AI analysis operations are processed asynchronously.

Instead of blocking the user while AI completes its work:

1. User uploads a document.
2. Backend immediately publishes an event to Kafka.
3. AI processing continues in the background.
4. Results are streamed back to the frontend.

This keeps the API responsive under heavy workloads.

---

## Transactional Outbox Pattern

To guarantee reliable message delivery, ReqAI implements the **Transactional Outbox Pattern**.

Benefits include:

- At-least-once message delivery
- No message loss
- Database consistency
- Crash recovery
- Kafka broker failure tolerance

Even if the server crashes immediately after saving data, pending events remain safely stored and will eventually be delivered.

---

## Redis Caching & Pub/Sub

Redis is used for two critical purposes.

### Caching

Frequently requested document analyses are cached to:

- Reduce database load
- Reduce API latency
- Improve response times

### Pub/Sub Messaging

Redis Pub/Sub enables lightweight communication between multiple backend instances in horizontally scaled deployments.

---

## Real-Time Streaming (Server-Sent Events)

Instead of inefficient HTTP polling, ReqAI uses **Server-Sent Events (SSE)**.

Benefits:

- Instant AI progress updates
- Live requirement generation
- Lower network overhead
- Better user experience

As the AI generates requirements, tasks, and test scenarios, the frontend receives them immediately.

---

## Dockerized Infrastructure

The complete development environment is containerized using Docker Compose.

Services include:

- Spring Boot Backend
- PostgreSQL
- Apache Kafka
- Zookeeper
- Redis
- Observability Stack (Grafana, Prometheus, Loki, Tempo)

This allows developers to bootstrap the entire infrastructure with a single command.

---

# 🎨 Frontend Features

The frontend is built using **Angular 17** with a focus on responsiveness and scalability.

### Enterprise UI

- Responsive SPA architecture
- Modern dashboard interface
- Seamless routing
- Clean user experience

### Dynamic State Management

Angular's `ChangeDetectorRef` is utilized to efficiently manage asynchronous SSE updates without disrupting Angular's change detection lifecycle.

### History Dashboard

Users can:

- Browse previous analyses
- Review generated requirements
- Track document history
- Reload previously processed documents

---

# ⚙️ Technology Stack

| Layer | Technologies |
|--------|--------------|
| Frontend | Angular 17, TypeScript (Hosted on **Vercel**) |
| Backend | Java 17, Spring Boot 3 (Hosted on **Render**) |
| Database | PostgreSQL |
| Messaging | Apache Kafka (Managed by **Confluent Cloud**) |
| Cache | Redis (Managed by **Upstash**) |
| Streaming | Server-Sent Events (SSE) |
| Observability | Grafana, Prometheus, Loki, Tempo (Hosted on **Oracle Cloud**) |
| Infrastructure | Docker, Docker Compose |
| AI Integration | OpenAI API |

---

# 🚀 Getting Started

## Prerequisites

Before running the project, install:

- JDK 17 or newer
- Node.js
- npm
- Docker Desktop

---

# 📦 Infrastructure Setup

Clone the repository.

```bash
git clone https://github.com/yourusername/reqai.git
cd reqai
```

Start all infrastructure services.

```bash
docker-compose up -d
```

This starts:

- PostgreSQL
- Kafka
- Zookeeper
- Redis
- Observability Stack

---

# ☕ Backend Setup

Navigate to the backend directory.

```bash
cd reqai-backend
```

Configure your AI API credentials inside:

```
application.yml
```

> **Note:** Store sensitive credentials using `.env` files or environment variables.

Run the Spring Boot application.

```bash
./mvnw spring-boot:run
```

---

# 🅰️ Frontend Setup

Navigate to the frontend.

```bash
cd reqai-frontend
```

Install dependencies.

```bash
npm install
```

Start the Angular development server.

```bash
ng serve
```

Open your browser:

```
http://localhost:4200
```

---

# 📂 Project Features

✅ AI Requirement Analysis

✅ Automatic Task Generation

✅ Business Requirement Extraction

✅ Test Scenario Generation

✅ Real-Time Progress Streaming (SSE)

✅ Kafka Event Processing

✅ Redis Caching

✅ Transactional Outbox Pattern

✅ Distributed Multi-Cloud Architecture (Vercel, Render, Oracle, Confluent, Upstash)

✅ Comprehensive Observability (Metrics, Logs, Traces)

✅ Dockerized Infrastructure

✅ Responsive Angular Dashboard

✅ Requirement History

---

# 💡 Why ReqAI?

ReqAI demonstrates several enterprise software engineering concepts frequently used in production systems:

- Clean Architecture
- Event-Driven Design
- Asynchronous Processing
- Distributed Messaging
- Reliable Event Delivery
- Real-Time Streaming
- Caching Strategies
- AI Integration
- Full-Stack Observability
- Multi-Cloud Deployment Strategy
- Containerized Deployment

The project serves as both a practical productivity tool and a showcase of modern backend engineering practices.

---

# 👨💻 Developer

## Mevlüt Can Erdem

**Software Development Intern**  

---

> Developed as part of an intensive enterprise software engineering internship focused on modern full-stack development, distributed systems, AI integration, and scalable software architecture.
