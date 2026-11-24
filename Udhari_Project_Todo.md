# Udhari Project Todo

**Goal:** Deliver the two-day digital lending demo described in the PRD, using Java 17+ (Java 21 suggested), Spring Boot, MySQL, Spring Security with JWT/BCrypt, and Thymeleaf as the only frontend template engine.

## Confirmed decisions

- **U01 — Project scope:** The demo covers the borrower and admin loan lifecycle: borrower registration/login and loan application, admin review, repayment schedule, and borrower repayment tracking. Bureau integration, real payments, disbursement, and loan agreement PDFs are out of scope.
- **U02 — Principal bounds:** INR 1,000 minimum and INR 100,000 maximum; the server validation and Thymeleaf form guidance must share these limits.
- **U03 — Interest rate:** 18% nominal annual rate, converted to a monthly rate by dividing by 12; EMI uses the reducing-balance formula.
- **U04 — Affordability:** Accept an application only when calculated EMI is at most 40% of the borrower's monthly income.
- **U05 — Duplicate applications:** Enforce per borrower across the demo (there is no product catalog). APPLIED and APPROVED loans count as active; REJECTED and CLOSED loans do not.
- **U06 — Repayment timing:** First instalment is due one month after approval. Later instalments use the same day of month, clamped to the last day when a month is shorter.
- **U07 — Payment eligibility:** Any PENDING instalment may be marked PAID for demo tracking; this does not process or represent a real payment.
- **U08 — Profile fields:** Do not collect PAN; it is not required for this demo.
- **U09 — Admin bootstrap:** On startup, optionally create the first ADMIN from `UDHARI_ADMIN_EMAIL` and `UDHARI_ADMIN_PASSWORD` environment variables when both are set and no admin exists. Never ship a default password.

## How to use this list

Work on one unchecked task at a time, in ID order. Each task starts only after the previous task is complete; do not start later work in parallel. A task is complete when its stated result exists and is usable by the next task. Tasks are intentionally small and ordered to avoid overlapping ownership of the same work.

## Day 1 — decisions, project foundation, and lending APIs

### Scope and project setup

- [x] **U01 — Confirm project scope.** Record the in-scope borrower and admin loan lifecycle and exclude bureau integration, real payments, disbursement, and loan agreement PDFs.
- [x] **U02 — Set loan input bounds.** Choose and record the permitted principal minimum and maximum; use the same values in validation and UI guidance.
- [x] **U03 — Set the interest rate.** Choose and record the annual or monthly rate convention and the numeric rate used for EMI calculations.
- [x] **U04 — Set the affordability rule.** Choose and record the income-to-EMI threshold used to accept or reject an application.
- [x] **U05 — Define duplicate-application behavior.** Specify which statuses count as active and whether the rule applies per borrower or per loan product; no product catalog is otherwise in scope.
- [x] **U06 — Define repayment timing.** Choose the first due-date rule and the monthly date rule for subsequent instalments.
- [x] **U07 — Define when payment is allowed.** Choose whether a borrower can mark any pending instalment paid or only an instalment whose due date has arrived.
- [x] **U08 — Confirm profile fields.** Decide whether PAN is collected, since the design documents mention it but the PRD's required registration fields do not.
- [x] **U09 — Confirm admin bootstrap.** Choose how the demo gets its first ADMIN account without exposing a default production password.
- [x] **U10 — Create the Git repository.** Initialize Git and add a `.gitignore` appropriate for Java, Maven, IDE files, and local secrets.
- [x] **U11 — Create the Maven Spring Boot application.** Set the application name to Udhari and choose compatible Spring Boot and Java versions.
- [x] **U12 — Add backend dependencies.** Include Spring Web, Spring Data JPA, MySQL Driver, Spring Security, Validation, Thymeleaf, JWT, Springdoc OpenAPI, and Spring Boot test support.
- [x] **U13 — Configure local application properties.** Set the MySQL connection, schema behavior, and application port; read database credentials and JWT signing secret from environment variables.
- [x] **U14 — Create the package skeleton.** Add the documented `controller`, `service`, `repository`, `entity`, `dto`, `security`, and `exception` packages under `com.sprint.lending`.
- [x] **U15 — Create the application entry point.** Add `LendingApplication` and confirm the app starts with local configuration and connects to MySQL 8.0.41.

### Data model and loan rules

- [x] **U16 — Implement the User entity.** Add ID, unique email, hashed-password storage, and BORROWER/ADMIN role.
- [x] **U17 — Implement the BorrowerProfile entity.** Add full name, monthly income, employment type, and the one-to-one User relationship; include PAN only if U08 selected it.
- [x] **U18 — Implement the Loan entity.** Add principal, interest rate, tenure, EMI, status, review remark, and borrower relationship.
- [x] **U19 — Implement the Repayment entity.** Add instalment number, due date, amount, PENDING/PAID status, and loan relationship.
- [x] **U20 — Create the user repository.** Support user lookup by email and duplicate-email checks.
- [x] **U21 — Create the borrower profile repository.** Support profile persistence and lookup needed by borrower workflows.
- [x] **U22 — Create the loan repository.** Support borrower-scoped loan reads and retrieval by ID for service checks.
- [x] **U23 — Create the repayment repository.** Support loan-scoped schedule reads and lookup by repayment ID.
- [x] **U24 — Define loan and repayment statuses.** Implement the documented states and allowed transitions in enums or equivalent types.
- [x] **U25 — Implement the EMI calculator.** Apply the PRD formula using the selected rate convention and consistent monetary rounding.
- [x] **U26 — Implement loan application validation.** Enforce required fields, positive principal, configured bounds, and 3–60 month tenure.
- [x] **U27 — Implement affordability validation.** Reject an application when its EMI violates the rule fixed in U04.
- [x] **U28 — Implement duplicate active application validation.** Apply the rule fixed in U05 before saving an application.
- [x] **U29 — Implement loan application service.** Persist a valid APPLIED loan with its calculated EMI and authenticated borrower.
- [x] **U30 — Implement borrower loan listing service.** Return only the authenticated borrower's loans.
- [x] **U31 — Implement repayment schedule generation.** Build monthly instalments using U06 and the approved loan's EMI, handling the final rounded instalment consistently.
- [x] **U32 — Implement admin approval service.** Change an APPLIED loan to APPROVED and persist its schedule atomically.
- [x] **U33 — Implement admin rejection service.** Change an APPLIED loan to REJECTED and save the required admin remark.
- [x] **U34 — Implement repayment read service.** Return a schedule only when the requested loan belongs to the authenticated borrower.
- [x] **U35 — Implement repayment payment service.** Apply U07, reject repeated payment, mark an eligible instalment PAID, and close the loan when all instalments are paid in one transaction.

### API and error handling

- [x] **U36 — Define registration DTOs.** Add request and response objects for user and borrower profile fields.
- [x] **U37 — Define loan DTOs.** Add request and response objects for loan application and loan-list data.
- [x] **U38 — Define review DTOs.** Add request objects for approval/rejection remarks and response data for admin review.
- [x] **U39 — Define repayment DTOs.** Add schedule and payment request/response objects.
- [x] **U40 — Implement the auth service.** Register user/profile and hash passwords with BCrypt; authenticate login credentials and issue a JWT.
- [x] **U41 — Implement the auth controller.** Expose `POST /api/auth/register` and `POST /api/auth/login` with validation.
- [x] **U42 — Implement the borrower loan controller.** Expose `POST /api/loans` and `GET /api/loans`.
- [x] **U43 — Implement the repayment controller.** Expose `GET /api/loans/{id}/repayments` and `PUT /api/repayments/{id}/pay`.
- [x] **U44 — Implement the admin loan controller.** Expose status-filtered `GET /api/admin/loans`, `PUT /api/admin/loans/{id}/approve`, and `PUT /api/admin/loans/{id}/reject`.
- [x] **U45 — Implement admin status filtering with Java Streams.** Filter the admin loan list by the optional status parameter using Streams as required by the assignment.
- [x] **U46 — Add domain exceptions.** Represent missing loans/repayments, invalid state changes, duplicate applications, and authorization failures clearly.
- [x] **U47 — Add consistent REST error responses.** Use one `@RestControllerAdvice` to return readable JSON errors and suitable HTTP status codes.

## Day 2 — security, Thymeleaf experience, and delivery

### Security and server-rendered pages

- [x] **U48 — Implement JWT utilities.** Create and validate signed tokens with the configured secret and chosen expiration.
- [x] **U49 — Implement the JWT request filter.** Parse bearer tokens and establish the authenticated principal for protected API requests.
- [x] **U50 — Configure endpoint authorization.** Keep registration/login public, restrict admin endpoints to ADMIN, and require authentication for borrower APIs.
- [x] **U51 — Enforce borrower ownership in services.** Confirm every loan and repayment operation checks the authenticated borrower's ownership.
- [x] **U52 — Add an admin bootstrap mechanism.** Create the first admin according to the safe approach selected in U09.
- [x] **U53 — Create shared Thymeleaf fragments.** Add the white navigation bar, role-specific links, and reusable page fragments.
- [x] **U54 — Create the login page.** Add labeled email/password inputs, validation feedback, and login submission to the REST API.
- [x] **U55 — Create the registration page.** Add labeled profile and credential fields, submit to registration, and display field errors.
- [x] **U56 — Create the borrower dashboard page.** Show loan summary counts derived from the borrower's loan list and the Apply for Loan action.
- [x] **U57 — Create the apply-loan page.** Collect principal and 3–60 month tenure, show estimated EMI when available, and submit to the loan API.
- [x] **U58 — Create the borrower loan list page.** Show principal, tenure, EMI, status chip, applicable remark, and schedule link.
- [x] **U59 — Create the repayment schedule page.** Show instalment number, due date, amount, status, and eligible mark-paid action.
- [x] **U60 — Create the admin applications page.** Show application summaries, status filters, and review actions from the admin API.
- [x] **U61 — Create the admin review interaction.** Show borrower/loan details, collect a remark, and provide explicit approve and reject actions.
- [x] **U62 — Add shared API request handling.** Use minimal same-origin Fetch calls with bearer JWT, readable error handling, and duplicate-submit prevention.
- [x] **U63 — Add logout behavior.** Clear the demo token from sessionStorage and return to the login page.
- [x] **U64 — Add the visual design tokens.** Apply the specified light canvas, white surfaces, blue primary, purple accent, text colors, spacing, rounded corners, and subtle borders.
- [x] **U65 — Add responsive and accessible behavior.** Stack cards on narrow screens, allow horizontal table scrolling, label inputs, show keyboard focus, and maintain readable contrast.
- [x] **U66 — Add loading, empty, error, and success states.** Cover the documented states and retain entered form data after API errors.
- [x] **U67 — Add role-aware view routing.** Serve the Thymeleaf pages and redirect expired or unauthorized sessions to the login view without rendering private data publicly.

### Documentation and handoff

- [x] **U68 — Publish OpenAPI documentation.** Configure Swagger UI at `/swagger-ui.html` and document the nine minimum REST endpoints.
- [x] **U69 — Create the database schema or seed script.** Include the schema setup and safe demo data needed to show borrower and admin flows.
- [x] **U70 — Create the Postman collection.** Include all nine minimum endpoints with representative requests and environment variables.
- [x] **U71 — Run the API verification pass.** Verified successful and invalid flows, role restrictions (401/403), borrower ownership (404/403), status filtering, approval schedule creation (12 instalments), repeated-payment rejection (409), and closure after all payments.
- [x] **U72 — Write the README.** Added prerequisites, environment and database setup, startup steps, API list, Mermaid ER diagram, Swagger URL, safe demo account setup, and Postman instructions.
- [x] **U73 — Run the end-to-end demo.** Completed registration/login, loan application, admin approval, three-installment schedule, all three payments, and confirmed CLOSED status.
- [x] **U74 — Commit the completed project.** Committed the source, schema, README, and Postman collection with the message `Complete Udhari lending demo`; `.env` remains ignored.

## Notes from the source documents

- The design uses Thymeleaf pages with minimal same-origin JavaScript; it does not call for a separate frontend application.
- Amount bounds, interest rate, affordability threshold, PAN requirement, and some repayment timing rules are intentionally left for Udhari's implementation to decide.
- The PRD suggests the two-day sequence, while this checklist breaks its deliverables into serial tasks so each can be completed and reviewed before the next begins.
