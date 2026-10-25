# CareerTrack — Job Application Tracker

A beginner-friendly full-stack job application tracker built with HTML, CSS, JavaScript, Java 17, Spring Boot, Spring Data JPA, and MySQL.

## Features

- Add, view, edit, and delete job applications
- Track company, role, location, salary range, job link, notes, applied date, interview date, and follow-up date
- Filter by status and search by company or role
- Dashboard counts for total applications, active assessments/interviews, interviews, and offers
- Responsive interface served directly by Spring Boot

## Run locally

### Easiest option: GitHub Codespaces — no installation

1. Open this repository on GitHub.
2. Click **Code** → **Codespaces** → **Create codespace on main**.
3. Wait for the workspace to open. It automatically starts a MySQL database.
4. In the Codespaces terminal, run:

   ```bash
   cd backend
   mvn spring-boot:run
   ```

5. Click the popup or open the **Ports** tab and select the link for port `8080`.

### Run on your own computer

1. Install Java 17+, Maven, and MySQL.
2. Start MySQL. The default configuration uses username `root` and password `root`.
3. If needed, set environment variables before starting: `DB_USERNAME`, `DB_PASSWORD`, and optionally `DB_URL`.
4. In a terminal, go into `backend` and run:

   ```bash
   mvn spring-boot:run
   ```

5. Open http://localhost:8080 in your browser. The app creates the `job_tracker` database when the MySQL user has permission.

Change the default database password in `backend/src/main/resources/application.properties` or set `DB_PASSWORD` for your local setup.

## REST endpoints

- `GET /api/applications` — list applications
- `GET /api/applications?status=INTERVIEW` — filter by status
- `GET /api/applications/{id}` — get one application
- `POST /api/applications` — create
- `PUT /api/applications/{id}` — update
- `DELETE /api/applications/{id}` — delete
- `GET /api/applications/summary` — dashboard counts

Statuses: `SAVED`, `APPLIED`, `ASSESSMENT`, `INTERVIEW`, `OFFER`, `REJECTED`, `WITHDRAWN`.

## Project layout

```text
job-application-tracker/
├── README.md
└── backend/
    ├── pom.xml
    └── src/main/
        ├── java/com/jahnavi/tracker/       # Spring Boot API
        └── resources/
            ├── application.properties      # MySQL config
            └── static/                     # HTML, CSS, JavaScript UI
```

The backend follows a simple controller → repository → MySQL flow. For a larger next iteration, add a service layer, user login, and per-user application ownership.
