/* ==============================================
   MASTER RESET SCRIPT (UPDATED HASHES)
   Run this in MySQL Workbench to wipe & reset.
   ============================================== */

-- 1. WIPE OLD DATA
DROP DATABASE IF EXISTS auth_db;
DROP DATABASE IF EXISTS erp_db;

-- 2. CREATE NEW DATABASES
CREATE DATABASE auth_db;
CREATE DATABASE erp_db;

-- 3. SETUP USERS (auth_db)
USE auth_db;

CREATE TABLE users_auth (
                            user_id INT AUTO_INCREMENT PRIMARY KEY,
                            username VARCHAR(50) NOT NULL UNIQUE,
                            password_hash VARCHAR(255) NOT NULL,
                            role ENUM('STUDENT', 'INSTRUCTOR', 'ADMIN') NOT NULL,
                            status ENUM('ACTIVE', 'LOCKED') NOT NULL DEFAULT 'ACTIVE',
                            last_login TIMESTAMP,
                            failed_attempts INT DEFAULT 0
);

-- Insert Users (Pre-filled with NEW hashes)
-- admin : adminpass
-- inst1 : instpass
-- stu1  : stupass1
-- stu2  : stupass2
INSERT INTO users_auth (username, password_hash, role) VALUES
                                                           ('admin', '$2a$12$IUT90s1Lk4B8J7OVIxhzKumWU.IwEdedYtEYuqfzBKYLFLPz.ez2y', 'ADMIN'),
                                                           ('inst1', '$2a$12$UqTZuMPxA9sq1VJ9Zur/SOPXHnd/cet48.jKAG0JZYMUDcjfwBlL.', 'INSTRUCTOR'),
                                                           ('stu1',  '$2a$12$Ko2nrom/uDko1yx.AHuAUuG4990JwRcF4Z8XtPhoTm0NPwsCsfbC', 'STUDENT'),
                                                           ('stu2',  '$2a$12$CvBx8Cs5lbp75sKCDPQm1.c8Xm0YzdAGiDxRwKWh/qIb4JvXvqhoG', 'STUDENT');

-- 4. SETUP APP DATA (erp_db)
USE erp_db;

CREATE TABLE students (
                          user_id INT PRIMARY KEY,
                          roll_no VARCHAR(20),
                          program VARCHAR(100),
                          year INT
);

CREATE TABLE instructors (
                             user_id INT PRIMARY KEY,
                             department VARCHAR(100),
                             title VARCHAR(100)
);

CREATE TABLE courses (
                         course_id INT AUTO_INCREMENT PRIMARY KEY,
                         code VARCHAR(20),
                         title VARCHAR(255),
                         credits INT
);

CREATE TABLE settings (
                          setting_key VARCHAR(50) PRIMARY KEY,
                          setting_value VARCHAR(100)
);

CREATE TABLE sections (
                          section_id INT AUTO_INCREMENT PRIMARY KEY,
                          course_id INT,
                          instructor_id INT,
                          day_time VARCHAR(100),
                          room VARCHAR(50),
                          capacity INT,
                          semester VARCHAR(50),
                          year INT,
                          FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE enrollments (
                             enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                             student_id INT,
                             section_id INT,
                             status VARCHAR(20) DEFAULT 'ENROLLED',
                             attendance INT DEFAULT 0,
                             FOREIGN KEY (student_id) REFERENCES students(user_id),
                             FOREIGN KEY (section_id) REFERENCES sections(section_id),
                             UNIQUE KEY unique_enrollment (student_id, section_id)
);

CREATE TABLE grades (
                        grade_id INT AUTO_INCREMENT PRIMARY KEY,
                        enrollment_id INT,
                        component VARCHAR(50),
                        score DOUBLE,
                        final_grade VARCHAR(2),
                        FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id),
                        UNIQUE KEY unique_grade (enrollment_id, component)
);

-- 5. INSERT SAMPLE DATA

-- Settings
INSERT INTO settings VALUES ('maintenance_on', 'false');
INSERT INTO settings VALUES ('deadline', '2025-12-31');

-- Profiles (matches to users_auth IDs: 1=admin, 2=inst1, 3=stu1, 4=stu2)
INSERT INTO instructors VALUES (2, 'Computer Science', 'Professor');
INSERT INTO students VALUES (3, '2025001', 'B.Tech CS', 2);
INSERT INTO students VALUES (4, '2025002', 'B.Tech ECE', 2);

-- Courses
INSERT INTO courses (code, title, credits) VALUES
                                               ('CS101', 'Introduction to Java', 4),
                                               ('CS201', 'Data Structures', 4),
                                               ('ECE250', 'Signals & Systems', 4);

-- Sections
INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) VALUES
                                                                                              (1, 2, 'MWF | 9:00 AM', 'LH-111', 50, 'Spring', 2026),
                                                                                              (2, 2, 'TTh | 11:00 AM', 'LH-001', 30, 'Spring', 2026),
                                                                                              (3, 2, 'MWF | 5:00 PM', 'C-201', 150, 'Spring', 2026);

-- Enrollments (stu1 in CS101)
INSERT INTO enrollments (student_id, section_id, attendance) VALUES (3, 1, 0);
