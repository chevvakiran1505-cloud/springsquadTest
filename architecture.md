# SPA + BFF (OIDC Token) + OAuth2 (Resource Server + DB) Architecture

> Modern secure web application architecture using the Backend-for-Frontend (BFF) pattern.

---

## Overview

```
                        ┌─────────────────────────┐
                        │    OIDC PROVIDER         │
                        │      (mock-auth)          │
                        │  • Authorization Server   │
                        │  • OpenID Connect         │
                        │  • OAuth 2.0              │
                        │  • Issues ID + Access Token│
                        └────────────┬─────────────┘
              3 /authorize ↑         │ 6 ID Token + Access Token (JWT)
                           │         ↓
┌──────────────────┐    ┌──────────────────────────────┐    ┌────────────────────────┐
│  BROWSER (SPA)   │    │   BFF (Backend-for-Frontend)  │    │   RESOURCE SERVER      │
│ React/Vue/Angular│◄──►│          Port 8080            │◄──►│   (OAuth2 RS)          │
│                  │7,8 │                               │9,10│   Port 8081            │
└──────────────────┘    └──────┬───────────────────────┘    └────────┬───────────────┘
                               │                                      │
                               ▼                              ┌───────┴───────┐
                        ┌──────────┐                          │               │
                        │ Session  │                   ┌──────▼─────┐  ┌──────▼──────┐
                        │  Store   │                   │Apache Kafka│  │  Oracle DB  │
                        │  Redis / │                   │ Port 9092  │  │  (21c XE)   │
                        │In-Memory │                   │  (KRaft)   │  │ Port 1521/22│
                        └──────────┘                   └────────────┘  └─────────────┘
```

---

## Sign-in Flow

| Step | Actor | Action |
|------|-------|--------|
| 1 | User | Opens SPA in browser |
| 2 | Server | SPA is delivered to browser |
| 3 | SPA → OIDC Provider | SPA redirects to `/authorize` |
| 4 | OIDC Provider → SPA | User authenticates and consents; provider returns authorization code |
| 5 | BFF → OIDC Provider | BFF exchanges authorization code for tokens at `/token` (Authorization Code Flow + PKCE) |
| 6 | OIDC Provider → BFF | ID Token + Access Token (JWT) returned to BFF |
| 7 | SPA → BFF | SPA makes API request to BFF at `/api/v1/…` (same origin) |
| 8 | BFF → SPA | BFF returns JSON response |
| 9 | BFF → Resource Server | BFF forwards request with `Bearer` Access Token |
| 10 | Resource Server → BFF | Resource Server returns JSON data |

---

## Components

### Browser — Single Page Application

**Framework:** React / Vue / Angular

**Responsibilities:**
- Renders UI
- Handles user interactions
- Calls BFF APIs (same origin only)
- Stores tokens in memory only

**Token handling:**
- Access Token held in memory
- ID Token held in memory
- No tokens written to `localStorage`
- All tokens cleared on page refresh or close

---

### OIDC Provider — mock-auth

A local identity provider standing in for a production IdP (e.g. Keycloak, Auth0, Okta).

**Capabilities:**
- Authorization Server
- OpenID Connect (OIDC)
- OAuth 2.0
- Issues ID Token and Access Token

---

### BFF — Backend-for-Frontend, Port 8080

The BFF is the browser's single entry point. It owns the OAuth2 session and shields the SPA from token handling entirely.

**Responsibilities:**
- Authenticates SPA using OIDC
- Stores tokens in server-side session (never in the browser)
- Calls Resource Server with Access Token
- Aggregates and shapes responses
- Hides backend complexity from the SPA
- CSRF protection and security headers

**Internal modules:**

| Module | Role |
|--------|------|
| Spring Security (OIDC Client) | Handles OIDC auth code flow and token exchange |
| Session Management | Persists tokens in server-side session |
| Token Manager | Manages token lifecycle and refresh |
| API Gateway / Proxy | Routes SPA requests to downstream services |
| Error Handling | Normalises and sanitises error responses |

---

### Session Store

Holds BFF server-side sessions so tokens never reach the browser.

**Options:** Redis (recommended for production) or in-memory (development only)

---

### Resource Server — OAuth2 Resource Server, Port 8081

The backend API. Validates every inbound request independently — it trusts the Bearer token, not the caller.

**Responsibilities:**
- Validates Access Token (JWT) on every request
- Authorises requests by scopes and roles
- Executes business logic
- Manages data access

**Internal layers:**

| Layer | Role |
|-------|------|
| Spring Security (OAuth2 RS) | JWT validation, scope/role enforcement |
| Service Layer | Business logic |
| Repository Layer | Database access via Oracle |
| Event Publisher | Publishes domain events to Kafka |

---

### Apache Kafka — Port 9092 (KRaft mode)

Async event bus for decoupling the resource server from downstream consumers.

**Key topics:**
- `transactions.completed`
- `events`

---

### Oracle DB — 21c XE, Port 1521 / 1522

Primary relational data store.

**Key schemas / tables:**
- `Accounts`
- `Transactions`
- `Users`

---

## Security Principles

- **No tokens in the browser.** Tokens are stored exclusively in the BFF's server-side session. The SPA holds only a session cookie.
- **BFF is the single API entry point.** The SPA never calls the resource server directly.
- **Resource Server validates every request.** Even requests arriving from the BFF are validated against the JWT signature and claims.
- **Authorization Code Flow + PKCE.** Prevents authorization code interception; no client secret is exposed in the SPA.
- **HTTPS everywhere.** All communication between every component is over TLS.
- **CSRF protection.** The BFF enforces CSRF headers on all mutating requests from the SPA.

---

## Network Legend

| Arrow | Meaning |
|-------|---------|
| `──►` | HTTP(S) request |
| `◄──` | HTTP(S) response |
| `━━►` | OIDC / OAuth2 flow |
| `- - ►` | Internal / infrastructure |

---

## Ports Reference

| Component | Port |
|-----------|------|
| BFF | 8080 |
| Resource Server | 8081 |
| Apache Kafka (KRaft) | 9092 |
| Oracle DB (21c XE) | 1521 / 1522 |
