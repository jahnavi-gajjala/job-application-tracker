# CareerTrack — Job Application Tracker

A full-stack web application for organizing a job search. CareerTrack combines a Java 17 / Spring Boot REST backend, MySQL persistence, and an HTML, CSS, and JavaScript dashboard served by the same application. Users can create an account, sign in, and manage applications associated with their account.

## Implemented features

- Visible signup and login forms, with the signed-in user's name and initial displayed in the dashboard.
- Create, view, update, and delete job applications.
- Store company, role, location, salary range, job URL, notes, applied date, follow-up date, and interview date.
- Seven application statuses: `SAVED`, `APPLIED`, `ASSESSMENT`, `INTERVIEW`, `OFFER`, `REJECTED`, and `WITHDRAWN`.
- Case-insensitive browser-side search by company or role; server-side status filtering through the REST API.
- Dashboard counts for total applications, assessments plus interviews (“In progress”), interviews, and offers.
- Session-based authentication, BCrypt password hashing, and controller-level ownership checks.
- Docker packaging with support for Render's `PORT` environment variable.

## Technology and architecture

| Area | Implementation |
| --- | --- |
| Frontend | HTML, CSS, vanilla JavaScript; Spring Boot static resources |
| Backend | Java 17, Spring Boot 3.3.5, Spring Web |
| Persistence | Spring Data JPA, Hibernate, MySQL Connector/J |
| Validation | Jakarta Bean Validation through Spring Boot Validation |
| Authentication | Servlet `HttpSession`; BCrypt via Spring Security Crypto |
| Build and container | Maven; multi-stage Dockerfile using Java 17 |

Request flow: browser → REST controller → Spring Data repository → MySQL. Authentication and application logic currently reside in the controllers; there is no separate service layer. The project uses Spring Security Crypto for password hashing, rather than a Spring Security filter-chain configuration.

## Run locally

### Prerequisites

- JDK 17 and Maven installed and available on your path.
- A running MySQL server and database credentials.
- Git to clone the repository.

```bash
git clone https://github.com/jahnavi-gajjala/job-application-tracker.git
cd job-application-tracker/backend
```

Set the database configuration in your terminal. Example for Bash:

```bash
export DB_URL='jdbc:mysql://localhost:3306/job_tracker?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export DB_USERNAME='your_mysql_user'
export DB_PASSWORD='your_mysql_password'
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080), select **Create an account**, and then add an application. Signup also signs you in.

| Variable | Default / behavior |
| --- | --- |
| `DB_URL` | Local MySQL on port 3306, database `job_tracker`; see `application.properties` for the full JDBC URL |
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | `root` |
| `PORT` | `8080` |

Use your own credentials. The default JDBC URL requests database creation, which requires suitable MySQL permissions; otherwise create the database first. Hibernate uses `ddl-auto=update` to update tables at startup. There is no database migration framework configured.

### GitHub Codespaces

The included dev container configures Java 17, installs Maven, and starts a MySQL 8.4 service with development credentials.

1. Open **Code → Codespaces → Create codespace on main**.
2. After container setup finishes, run `cd backend` and `mvn spring-boot:run`.
3. Open the forwarded port `8080` from the **Ports** tab.

The database connection variables are supplied by `.devcontainer/docker-compose.yml`; the application itself is started manually.

## REST API overview

Requests and responses use JSON, except successful logout and delete responses, which have no body. Application endpoints require the session cookie established by signup or login. Browser requests use the same origin; API clients must retain and resend cookies.

| Method | Path | Behavior |
| --- | --- | --- |
| POST | `/api/auth/signup` | Accept `name`, `email`, `password`; create an account and session; return profile (201) |
| POST | `/api/auth/login` | Accept `email`, `password`; establish session; return profile |
| GET | `/api/auth/me` | Return signed-in profile: `id`, `name`, `email` |
| POST | `/api/auth/logout` | Invalidate the session (204); available through the API, with no logout control currently wired in the UI |
| GET | `/api/applications` | List the current user's applications |
| GET | `/api/applications?status=INTERVIEW` | List the current user's applications matching an uppercase status |
| GET | `/api/applications/{id}` | Read one owned application |
| POST | `/api/applications` | Create an application owned by the signed-in user (201) |
| PUT | `/api/applications/{id}` | Update the fields of an owned application |
| DELETE | `/api/applications/{id}` | Delete an owned application (204) |
| GET | `/api/applications/summary` | Return `total` and lowercase count keys for every status |

Example create/update body:

```json
{
  "company": "Example Company",
  "role": "Software Engineer",
  "location": "Hyderabad",
  "salaryRange": "To be discussed",
  "jobUrl": "https://example.com/jobs/123",
  "appliedDate": "2026-10-30",
  "status": "APPLIED",
  "followUpDate": "2026-11-06",
  "interviewDate": null,
  "notes": "Application submitted."
}
```

Company and role must be nonblank. Date fields use `YYYY-MM-DD`; use the uppercase status values listed above. PUT replaces the editable fields, so send the complete intended application data.

## Authentication and account ownership

- Signup trims names, trims and lowercases email addresses, checks for an existing email without case sensitivity, and stores a BCrypt password hash.
- Signup validates a nonblank name (maximum 80 characters), an email address, and a password of 6–72 characters. Login validates email/password input and checks the password hash.
- Signup and login store the user's ID in the server-side HTTP session. Profile responses contain only ID, name, and email.
- Application controllers resolve the current user from that session. Lists, status filters, and summary counts are scoped to that user; creation assigns the owner on the server.
- Reading, updating, or deleting an ID requires a matching owner. Missing records, records owned by another user, and records with no owner return 404. Requests without a valid signed-in user return 401.
- Duplicate signup email returns 409; incorrect login credentials return 401.
- Existing applications with no owner are not automatically assigned to an account.

These describe the implemented checks, not a production security audit. Email verification, password reset, and persistent/shared session storage are not implemented in the current source.

## Project structure

| Path | Responsibility |
| --- | --- |
| `backend/pom.xml` | Dependencies, Java version, Spring Boot packaging |
| `backend/Dockerfile` | Maven build stage and Java runtime stage |
| `backend/src/main/java/com/jahnavi/tracker/TrackerApplication.java` | Spring Boot entry point |
| `backend/src/main/java/com/jahnavi/tracker/application/AuthController.java` | Signup, login, logout, current profile |
| `backend/src/main/java/com/jahnavi/tracker/application/JobApplicationController.java` | Application CRUD, ownership checks, summaries |
| `backend/src/main/java/com/jahnavi/tracker/application/JobApplication.java`, `User.java` | JPA entities |
| `backend/src/main/java/com/jahnavi/tracker/application/JobApplicationRepository.java`, `UserRepository.java` | Spring Data repositories |
| `backend/src/main/resources/application.properties` | Database settings, schema update mode, port |
| `backend/src/main/resources/static/` | `index.html`, `styles.css`, `app.js` |
| `.devcontainer/` | Codespaces / dev-container configuration and MySQL service |
| `.github/workflows/build.yml` | Maven packaging workflow for pushes and pull requests |

## Docker and Render deployment

The Dockerfile expects the **repository root** as its build context because it copies `backend/pom.xml` and `backend/src`.

Build and run locally from the repository root:

```bash
docker build -f backend/Dockerfile -t careertrack .
docker run --rm -p 8080:8080 \
  -e DB_URL -e DB_USERNAME -e DB_PASSWORD \
  careertrack
```

Export the database variables before running this command. The MySQL address must be reachable from inside the container; `localhost` inside it refers to the container itself.

For a Render Docker web service:

1. Connect this repository and select the intended branch.
2. Keep the build context at the repository root; use `backend/Dockerfile` as the Dockerfile path. Do not set the service root directory to `backend` with this Dockerfile.
3. Configure `DB_URL` as a JDBC MySQL URL and provide `DB_USERNAME` and `DB_PASSWORD` for a reachable MySQL database.
4. Use the Dockerfile's startup command. It runs `java -jar app.jar --server.port=${PORT:-8080}`; `application.properties` also supports `PORT`.
5. After deployment, verify the service URL, signup/login, and application operations before publishing a demo link.

The image packages the app with `mvn -B -DskipTests package` and runs it on a Java 17 JRE. The repository has Docker/port support; that alone does not verify a successful Render deployment. No verified live demo URL is published in this README.

## Build verification and current limitations

- The repository includes a GitHub Actions packaging workflow that runs `mvn -B -DskipTests package`.
- A [successful push workflow run](https://github.com/jahnavi-gajjala/job-application-tracker/actions/runs/40363542022) was directly verified for [commit `58e93e4`](https://github.com/jahnavi-gajjala/job-application-tracker/commit/58e93e4efccb24c3291b672c54140c44f75cdec9). This is build evidence only: the workflow skips tests.
- No automated test suite is present in the inspected repository tree. No test coverage or runtime verification is claimed here.
- The browser status dropdown currently supplies title-case values while its JavaScript comparison uses uppercase API statuses. The REST status filter is implemented; the browser dropdown needs normalization.
- A separate service layer remains a possible refactor. Signup/login and per-user ownership are already implemented.

## Screenshots and demo

**Live demo — placeholder:** Add a verified public URL after checking the deployed application.

**Screenshots — placeholders:** Add actual captures of:
- Signup / login screen.
- Signed-in dashboard with sample applications.
- Add / edit application form.

No screenshot assets or demo links are implied by these placeholders.
