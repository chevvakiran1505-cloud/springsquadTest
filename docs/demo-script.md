# demo-script.md

# Secure Banking App — Demo Script

| Section | Owner | Time | Talking Points | Actions |
|---|---|---|---|---|
| Architecture Intro | Fatima | 2 min | Explain React frontend, backend/API, Auth service, PostgreSQL, Kafka, Admin module. Point to where secure cookies, Bearer tokens, and Kafka are used. Mention HttpOnly + Secure cookies and event-driven architecture. | Show architecture diagram |
| Login Flow | Fatima | 3 min | Show secure session cookie. Highlight HttpOnly, Secure, SameSite. Show Local Storage and Session Storage are empty to demonstrate protection against XSS token theft. Optionally show `Set-Cookie` in Network tab. | Login as `alice` → Open DevTools → Show Cookies + Storage |
| Customer Flow | Rama | 5 min | Explain authorization checks and account ownership validation. Show deposit validation and transactional transfers. Switch to Kafka logs/UI and show events like `DepositCreated`, `TransferCompleted`, and audit events. Explain auditability and decoupled services. | Open Accounts → View Account → Deposit → Internal Transfer → Watch Kafka events |
| Admin Flow | Rama | 2 min | Show role-based access control. Admin can access management pages. Alice receives `403 Forbidden`. Emphasize backend-enforced authorization, not just hidden UI routes. | Logout alice → Login as admin → Open `/admin/users` → Logout → Login as alice → Retry `/admin/users` |
| SAST Highlight | Kiran | 2 min | Example: SQL injection or unsanitized input. Explain what scanner detected, why it mattered, and how parameterized queries/input validation fixed it. Show reduced findings after rescan. | Show one finding → Show vulnerable code → Show fix → Show clean rescan |
| DAST Highlight | Kiran | 2 min | Example payload: `<script>alert(1)</script>`. Show validation errors or blocked request. Highlight no stack trace leakage and proper sanitization. Explain runtime protection testing. | Show custom malicious payload → Send request → Show safe response |
| Q&A | Everyone | 2 min | Be ready for questions on Kafka, cookies vs JWT storage, CSRF protection, password hashing, transactions, Kafka failures, OWASP testing, SAST/DAST tools, and authorization strategy. | Answer audience questions |

---

# Suggested Closing Line

> “Our project demonstrates secure authentication, role-based authorization, event-driven architecture using Kafka, and integrated security testing through both SAST and DAST practices.”
