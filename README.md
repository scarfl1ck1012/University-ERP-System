University ERP Project

Overview
This project is a desktop-based University ERP application developed in Java using Swing for the user interface and MySQL for data storage. It allows three types of users (Student, Instructor, Admin) to log in and perform different tasks related to course registration, grading, and management.

Features

Login System: Secure login with password hashing (BCrypt).

Student Module:

View Course Catalog.

Register for courses (checks capacity and duplicates).

View Timetable.

View Grades and download transcript (CSV).

Drop courses.

Instructor Module:

View assigned sections.

Enter grades for students.

View class statistics.

Admin Module:

Create new users (Student/Instructor).

Create new courses and sections.

Update section details (room, capacity).

Toggle Maintenance Mode (blocks changes for non-admins).

Prerequisites
To run this project, you need the following installed on your system:

Java Development Kit (JDK) version 11 or higher.

MySQL Server.

MySQL Workbench (optional, for easier database setup).

An IDE like VS Code, IntelliJ IDEA, or Eclipse.

Maven 

Database Setup
The application requires two databases: auth_db and erp_db.

Open MySQL Workbench.

Open the file "final_setup.sql" located in the root folder of this project.

Execute the entire script to create the databases and tables, and to insert sample data.

Configuration
Before running the application, you must configure the database connection settings.

Navigate to the folder: src/main/resources

Open the file "config.properties".

Update the "user" and "password" fields to match your local MySQL credentials. The default is set to "root" for both user and password.

How to Run

Open the project folder in your IDE.

Allow Maven to download the necessary dependencies (listed in pom.xml).

Locate the main file: src/main/java/edu/univ/erp/App.java

Run the App.java file.

Login Credentials
Use the following sample accounts to test the application. All passwords are set to "pass".

Role: Admin Username: admin1 Password: adminpass

Role: Instructor Username: inst1 Password: instpass

Role: Student Username: stu1 Password: stupass1

Role: Student Username: achintya shukla Password: achintya

Project Structure

src/main/java/edu/univ/erp: Contains the main application code.

auth: Authentication logic.

data: Database connection helper.

domain: Data models (like SectionDetails).

service: Business logic (StudentService, AdminService, etc.).

ui: User interface screens (organized by role).

util: Helper classes (like CsvExporter).

src/main/resources: Contains configuration files and images.

final_setup.sql: SQL script to initialize the database.

Notes

If the application shows "Maintenance Mode is ON", log in as an Admin and turn it off in the System Settings tab.

Duplicate registrations are prevented by the database.

Course capacity must be greater than 0.