# stats-findings.md

| Finding ID | Severity | Category | Decision | Rationale |
|---|---|---|---|---|
| SAST-001 | Medium | Maintainability | Accept / Fix Later | Deprecated method is marked for removal and may break compatibility in future releases. Replace with the supported alternative during planned maintenance. |
| SAST-002 | Low | Maintainability | Accept / Fix Later | Usage of deprecated `deserialize` does not create an immediate functional or security risk, but should be updated during routine maintenance. |
| SAST-003 | Blocker | Security | Accept / Fix Later | Critical security finding identified. Remediation is required and should be prioritized to reduce potential operational or security impact. |
| SAST-004 | High | Security | Fixed | Hardcoded credentials identified in `application.yml` for mock authentication (`client-id` and `client-secret`) were remediated by replacing sensitive values with environment variables, eliminating credential exposure from source code. |
