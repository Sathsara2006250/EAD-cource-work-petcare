-- =====================================================================
-- Pet Care & Veterinary Services Management System - Database Schema
-- Compatible with MySQL / MariaDB (XAMPP / Standalone)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `petcare_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `petcare_db`;

-- Drop existing tables in reverse dependency order if resetting
DROP TABLE IF EXISTS `treatments`;
DROP TABLE IF EXISTS `appointments`;
DROP TABLE IF EXISTS `pets`;
DROP TABLE IF EXISTS `customers`;
DROP TABLE IF EXISTS `users`;

-- 1. Users Table (Role-based authentication: Manager and Customer)
CREATE TABLE `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `role` ENUM('Manager', 'Customer') NOT NULL DEFAULT 'Customer',
    `email` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Customers Table (Pet Owners)
CREATE TABLE `customers` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    `registration_date` DATE NOT NULL,
    CONSTRAINT `fk_customer_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Pets Table
CREATE TABLE `pets` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` INT NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `species` VARCHAR(30) NOT NULL,
    `breed` VARCHAR(50) NOT NULL,
    `age` INT NOT NULL,
    `gender` ENUM('Male', 'Female') NOT NULL,
    `weight` DECIMAL(5, 2) NOT NULL,
    `medical_notes` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_pet_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Appointments Table (Transaction 1)
CREATE TABLE `appointments` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `appointment_code` VARCHAR(20) NOT NULL UNIQUE,
    `customer_id` INT NOT NULL,
    `pet_id` INT NOT NULL,
    `vet_doctor` VARCHAR(100) NOT NULL,
    `appointment_date` DATE NOT NULL,
    `appointment_time` VARCHAR(10) NOT NULL,
    `status` ENUM('Scheduled', 'In-Progress', 'Completed', 'Cancelled') NOT NULL DEFAULT 'Scheduled',
    `reason` VARCHAR(255) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_appointment_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_appointment_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Treatments & Medical Billing Table (Transaction 2)
CREATE TABLE `treatments` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `appointment_id` INT NOT NULL UNIQUE,
    `diagnosis` VARCHAR(255) NOT NULL,
    `procedure_details` TEXT NOT NULL,
    `medication_prescribed` TEXT,
    `medication_cost` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `treatment_cost` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `total_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `treatment_date` DATE NOT NULL,
    `status` ENUM('Pending', 'Paid', 'Waived') NOT NULL DEFAULT 'Paid',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_treatment_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- Seed Initial Data
-- =====================================================================

-- Users
INSERT INTO `users` (`id`, `username`, `password`, `full_name`, `role`, `email`, `phone`) VALUES
(1, 'admin', 'admin123', 'Dr. Robert Jenkins (Manager)', 'Manager', 'admin@petcare.com', '0771234567'),
(2, 'john_doe', 'user123', 'John Doe', 'Customer', 'john.doe@gmail.com', '0712345678'),
(3, 'sarah_w', 'user123', 'Sarah Williams', 'Customer', 'sarah.w@hotmail.com', '0723456789'),
(4, 'emily_c', 'user123', 'Emily Clark', 'Customer', 'emily.clark@yahoo.com', '0756789012');

-- Customers
INSERT INTO `customers` (`id`, `user_id`, `full_name`, `email`, `phone`, `address`, `registration_date`) VALUES
(1, 2, 'John Doe', 'john.doe@gmail.com', '0712345678', '124 Maple Avenue, Springfield', '2025-01-10'),
(2, 3, 'Sarah Williams', 'sarah.w@hotmail.com', '0723456789', '78 Pine Ridge Road, Oakville', '2025-01-15'),
(3, 4, 'Emily Clark', 'emily.clark@yahoo.com', '0756789012', '45 Highland Street, Lakeview', '2025-02-01'),
(4, NULL, 'David Miller', 'david.m@outlook.com', '0789012345', '12 Elm Street, Riverdale', '2025-02-14');

-- Pets
INSERT INTO `pets` (`id`, `customer_id`, `name`, `species`, `breed`, `age`, `gender`, `weight`, `medical_notes`) VALUES
(1, 1, 'Buddy', 'Dog', 'Golden Retriever', 3, 'Male', 28.5, 'Allergic to specific flea collar brands. Fully vaccinated.'),
(2, 1, 'Luna', 'Cat', 'Siamese', 2, 'Female', 4.2, 'Routine checkups up to date.'),
(3, 2, 'Max', 'Dog', 'German Shepherd', 5, 'Male', 34.0, 'Mild hip dysplasia history, monitored regularly.'),
(4, 3, 'Milo', 'Cat', 'Persian', 1, 'Male', 3.8, 'Sensative stomach, prescribed gastro food.'),
(5, 4, 'Bella', 'Dog', 'Beagle', 4, 'Female', 11.2, 'Ear infection treated in Nov 2024.');

-- Appointments
INSERT INTO `appointments` (`id`, `appointment_code`, `customer_id`, `pet_id`, `vet_doctor`, `appointment_date`, `appointment_time`, `status`, `reason`) VALUES
(1, 'APT-1001', 1, 1, 'Dr. Robert Jenkins', '2025-03-01', '09:00 AM', 'Completed', 'Annual Health Checkup & Vaccination'),
(2, 'APT-1002', 2, 3, 'Dr. Robert Jenkins', '2025-03-02', '10:30 AM', 'Completed', 'Hip joint pain evaluation and mobility therapy'),
(3, 'APT-1003', 3, 4, 'Dr. Alicia Stone', '2025-03-05', '02:00 PM', 'Completed', 'Gastrointestinal upset and dietary consultation'),
(4, 'APT-1004', 1, 2, 'Dr. Alicia Stone', '2025-03-10', '11:00 AM', 'Scheduled', 'Routine dental cleaning'),
(5, 'APT-1005', 4, 5, 'Dr. Robert Jenkins', '2025-03-15', '03:30 PM', 'Scheduled', 'Ear canal irritation inspection');

-- Treatments & Billing
INSERT INTO `treatments` (`id`, `appointment_id`, `diagnosis`, `procedure_details`, `medication_prescribed`, `medication_cost`, `treatment_cost`, `total_amount`, `treatment_date`, `status`) VALUES
(1, 1, 'Healthy, booster vaccines administered', 'Physical examination, Rabies booster & DHPP vaccination injection', 'Deworming tablet (Drontal Plus)', 25.00, 50.00, 75.00, '2025-03-01', 'Paid'),
(2, 2, 'Mild osteoarthritis in left hip', 'X-Ray imaging, orthopedic joint manipulation, anti-inflammatory laser session', 'Carprofen 75mg (14 days supply), Glucosamine supplements', 45.00, 120.00, 165.00, '2025-03-02', 'Paid'),
(3, 3, 'Acute gastritis secondary to food change', 'Abdominal ultrasound, hydration therapy IV, anti-emetic injection', 'Metronidazole 250mg, Probiotic gastro paste', 30.00, 85.00, 115.00, '2025-03-05', 'Paid');
