CREATE DATABASE IF NOT EXISTS medicine_db;
USE medicine_db;

CREATE TABLE IF NOT EXISTS users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  phone VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS medicine (
  medicine_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  name VARCHAR(50) NOT NULL,
  dosage VARCHAR(30),
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS dose_time (
  dose_id INT AUTO_INCREMENT PRIMARY KEY,
  medicine_id INT NOT NULL,
  dose_time TIME NOT NULL,
  FOREIGN KEY (medicine_id) REFERENCES medicine(medicine_id) ON DELETE CASCADE
);

-- Optional sample data
INSERT INTO users (name, phone) VALUES ('Ravi', '9876543210');
INSERT INTO medicine (user_id, name, dosage) VALUES (1, 'Paracetamol', '500 mg');
