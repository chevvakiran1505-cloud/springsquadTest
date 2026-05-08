# Environment Setup & Startup Guide

## Environment Prerequisites

| Tool | Required Version |
| ------------- | ------------- |
| JDK  | 17 |
| Maven | 3.9+ |
|Node.js | 18+ |
|  Oracle XE  |  21c |
| Apache Kafka| 4.x (KRaft mode) |
| Docker/Desktop (optional)|  Latest stable |
---------------

# Required Environment Variables

Use the provided `.env.example` as the source of truth.

Typical variables you must configure include:

  | Variable                | Purpose                   |
| ----------------------- | ------------------------- |
| JAVA_HOME               | JDK 17 installation path  |
| MAVEN_HOME              | Maven installation path   |
| ORACLE_HOST             | Oracle hostname           |
| ORACLE_PORT             | Oracle listener port      |
| ORACLE_SERVICE          | Usually XEPDB1            |
| ORACLE_USER             | bankapp                   |
| ORACLE_PASSWORD         | Database password         |
| KAFKA_BOOTSTRAP_SERVERS | Usually localhost:9092    |
| OIDC_ISSUER_URI         | Mock auth issuer URL      |
| BFF_PORT                | Backend-for-frontend port |
| RESOURCE_SERVER_PORT    | Resource server port      |
| FRONTEND_PORT           | Vite frontend port        |


Copy and customize:

    cp .env.example .env

---------------

# Exact Service Startup Order

1\. mock-auth

2\. resource-server

3\. bff

4\. frontend

---------------

# Service Ports

| Service                | Port |
| ---------------------- | ---- |
| Mock Auth              | 9000 |
| BFF                    | 8080 |
| Resource Server        | 8081 |
| Frontend (Vite)        | 5173 |
| Kafka                  | 9092 |
| Oracle XE (Docker)     | 1521 |
| Oracle XE (Standalone) | 1522 |
| WireMock (optional)    | 8089 |


---------------

# Starting the Services

## Mock Auth

    cd mock-auth
    mvn spring-boot:run

## Resource Server

    cd resource-server
    mvn spring-boot:run

## BFF

    cd bff
    mvn spring-boot:run

## Frontend

    cd frontend
    npm install
    npm run dev

---------------

# Running Tests

## Backend Tests

    mvn test

## Frontend Tests

    npm test

or

    npm run test

---------------

# Demo Credentials

| Role          | Username | Password |
| ------------- | -------- | -------- |
| Standard User | alice    | password |
| Admin User    | admin    | password |


---------------

# Smoke Test Checklist

| Endpoint                                                                                                             | Expected        |
| -------------------------------------------------------------------------------------------------------------------- | --------------- |
| GET [http://localhost:9000/.well-known/openid-configuration](http://localhost:9000/.well-known/openid-configuration) | 200             |
| GET [http://localhost:8081/health](http://localhost:8081/health)                                                     | 200             |
| GET [http://localhost:8080/health](http://localhost:8080/health)                                                     | 200             |
| GET [http://localhost:8081/api/v1/accounts](http://localhost:8081/api/v1/accounts)                                   | 401             |
| GET [http://localhost:8080/api/v1/accounts](http://localhost:8080/api/v1/accounts)                                   | 401 or redirect |
| [http://localhost:5173](http://localhost:5173)                                                                       | SPA loads       |

