# University ERP System

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white) 
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white) 
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

## Overview
The **University ERP System** is a robust, desktop-based enterprise resource planning application designed for educational institutions. Developed in **Java** utilizing **Swing** for a responsive user interface and **MySQL** for persistent data storage, the application serves three primary roles—Student, Instructor, and Administrator—providing role-specific tools for course registration, grading, and systemic management.

## ✨ Features

### 🔒 Core System
* **Secure Authentication**: End-to-end encrypted login utilizing password hashing (BCrypt).
* **Multi-threaded UI**: Core database operations use SwingWorker to ensure a non-blocking, responsive interface.
* **Maintenance Mode**: Admins can temporarily lock down mutations and access for system maintenance.

### 🎓 Student Module
* View the full **Course Catalog**.
* **Register for Courses** (with real-time capacity and duplicate checking).
* View personal **Timetable** and scheduled sessions.
* Access **Grades** and download academic transcripts in CSV format.
* **Drop courses** seamlessly.

### 👨‍🏫 Instructor Module
* View assigned teaching **Sections**.
* Input, update, and save **Student Grades**.
* Review comprehensive **Class Statistics** and grade distributions.

### 🛡️ Admin Module
* Provision new **Users** (Students & Instructors).
* Manage the curriculum: Create new **Courses** and scheduled **Sections**.
* Update section logistics (Capacity, Room assignments).
* Toggle overarching **Maintenance Mode**.

---

## 🛠️ Tech Stack
* **Language**: Java 11+
* **GUI Framework**: Java Swing (with MigLayout & FlatLaf)
* **Database**: MySQL 8.x
* **Build Tool**: Maven

---

## 🚀 Getting Started

### Prerequisites
Before running the application, ensure you have the following installed:
1. **Java Development Kit (JDK)** version 11 or higher.
2. **MySQL Server**.
3. **Maven** (optional, recommended for dependency management).
4. **Git** (for version control).
5. Any Java-compatible IDE (e.g., VS Code, IntelliJ IDEA, Eclipse).

### Database Setup
The application interfaces with two localized databases: `auth_db` and `erp_db`.
1. Open MySQL Workbench or your preferred MySQL GUI.
2. Locate the file `final_setup.sql` in the project's root folder.
3. Execute the entire SQL script to scaffold the databases, configure tables, and inject sample data.

### Configuration
Update the database connection properties to match your local SQL server credentials:
1. Navigate to: `src/main/resources/config.properties`.
2. Update the `user` and `password` fields (default is usually `root` / `root`).

```properties
auth.db.url=jdbc:mysql://localhost:3306/auth_db
auth.db.user=root
auth.db.pass=root

erp.db.url=jdbc:mysql://localhost:3306/erp_db
erp.db.user=root
erp.db.pass=root
```

### Running the Application
1. Clone the repository and open the project root in your IDE.
2. Sync/Download Maven dependencies specified in `pom.xml`.
3. Run the main application file located at: `src/main/java/edu/univ/erp/App.java`

---

## 🔑 Demo Login Credentials
To quickly explore the application, use these pre-configured accounts:

| Role | Username | Password |
|---|---|---|
| **Admin** | `admin1` | `adminpass` |
| **Instructor** | `inst1` | `instpass` |
| **Student** | `stu1` | `stupass1` |
| **Student** | `achintya shukla` | `achintya` |

---

## 📂 Project Structure

```
src/main/java/edu/univ/erp
 ├── auth/      # Authentication logic & BCrypt wrappers
 ├── data/      # Database connection pooling & config loaders
 ├── domain/    # Data Transfer Objects (e.g., SectionDetails)
 ├── service/   # Core Business logic (StudentService, AdminService, InstructorService)
 ├── ui/        # Swing UI Modules partitioned by Role (admin, instructor, student, auth)
 └── util/      # Global utility extensions (e.g., CsvExporter)
```

## 📝 Usage Notes
* If your login presents a **"Maintenance Mode is ON"** alert, log in using the Admin credentials and disable it via the System Settings dashboard.
* The system enforces foreign-key and logic checks: Duplicate registrations are rejected, and courses must have a capacity `> 0`.