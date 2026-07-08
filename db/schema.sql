CREATE DATABASE smartpark_db;
USE smartpark_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE customers (
    user_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE employees (
    user_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(15) UNIQUE NOT NULL,
    employee_type VARCHAR(30) NOT NULL,
    shift VARCHAR(20),
    salary DOUBLE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(user_id) ON DELETE CASCADE
);

CREATE TABLE parking_floors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    floor_number INT UNIQUE NOT NULL,
    total_capacity INT NOT NULL
);

CREATE TABLE parking_slots (
    id INT AUTO_INCREMENT PRIMARY KEY,
    floor_id INT NOT NULL,
    slot_number VARCHAR(10) UNIQUE NOT NULL,
    slot_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    FOREIGN KEY (floor_id) REFERENCES parking_floors(id) ON DELETE CASCADE
);

CREATE TABLE pricing (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_type VARCHAR(20) UNIQUE NOT NULL,
    base_rate DOUBLE NOT NULL,
    hourly_rate DOUBLE NOT NULL
);

CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    slot_id INT NOT NULL,
    reservation_time DATETIME NOT NULL,
    expiry_time DATETIME NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (customer_id) REFERENCES customers(user_id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    FOREIGN KEY (slot_id) REFERENCES parking_slots(id)
);

CREATE TABLE parking_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT,
    vehicle_id INT NOT NULL,
    slot_id INT NOT NULL,
    entry_time DATETIME NOT NULL,
    exit_time DATETIME,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    FOREIGN KEY (slot_id) REFERENCES parking_slots(id)
);

CREATE TABLE bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    amount DOUBLE NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    billing_time DATETIME NOT NULL,
    FOREIGN KEY (session_id) REFERENCES parking_sessions(id)
);

CREATE TABLE feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    rating INT NOT NULL,
    comments TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(user_id)
);

CREATE TABLE salary_disbursements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    amount DOUBLE NOT NULL,
    payment_date DATE NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(user_id) ON DELETE CASCADE
);