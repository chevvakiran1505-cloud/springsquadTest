Security Decisions

# BFF over Pure SPA

We chose the Backend-for-Frontend (BFF) pattern instead of a pure Single Page Application (SPA) with OAuth2 in the browser to reduce the attack surface. In a pure SPA, access tokens are stored in the browser and exposed to XSS risks. With the BFF approach, all OAuth2 interactions are handled server-side, and the browser only communicates with the BFF using a session cookie. This significantly improves security by keeping tokens out of the client.

# Token Storage

OAuth2 access and refresh tokens are stored exclusively on the server using Spring Security’s OAuth2AuthorizedClient (backed by the HTTP session). Tokens are never exposed to the browser, localStorage, or sessionStorage. The browser only holds a JSESSIONID cookie (HttpOnly), which references the server-side session where tokens are stored.

# CSRF Protection

CSRF protection is implemented using Spring Security’s CookieCsrfTokenRepository. A non-HttpOnly XSRF-TOKEN cookie is issued to the browser, which the SPA reads and sends back in the X-XSRF-TOKEN header for all state-changing requests (POST, PUT, DELETE). Requests missing or containing an invalid CSRF token are rejected with HTTP 403.

# BFF → Resource Server Authentication

The BFF communicates with the Resource Server using Spring’s WebClient configured with ServletOAuth2AuthorizedClientExchangeFilterFunction. This automatically attaches the current user’s access token to outgoing requests in the Authorization: Bearer header. This ensures that the Resource Server receives a properly authenticated request without exposing tokens to the frontend.

# RBAC Enforcement

Role-Based Access Control (RBAC) is enforced at multiple layers in the Resource Server: URL-level security using Spring Security configuration, method-level security using annotations like @PreAuthorize, and service-layer ownership checks to ensure users can only access their own resources. Unauthorized access to another user’s resource returns HTTP 404 to avoid leaking resource existence.

# Payment Processor API Key Handling

The payment processor API key is stored as an environment variable and injected into the application at runtime. It is never hard-coded in source code or configuration files. The key is not logged or exposed in error messages. Access to this key is limited to the specific service that requires it.

# Hardening To-Do (Future Improvements)

The following improvements are identified for production readiness: replace in-memory session storage with a distributed session store (e.g., Redis), enforce CSRF protection on logout endpoint, add rate limiting for authentication and transfer endpoints, implement audit logging for security-sensitive actions, enable stricter HTTP security headers (HSTS, CSP), and rotate and securely manage secrets using a vault solution.