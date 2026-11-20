CREATE TABLE patients (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(254) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  age INT NULL,
  registered_at DATETIME(6) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT uk_patients_email UNIQUE (email)
);

CREATE TABLE admin_accounts (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(254) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT uk_admin_accounts_email UNIQUE (email)
);

CREATE TABLE doctors (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  specialization VARCHAR(100) NOT NULL,
  email VARCHAR(254) NULL,
  password_hash VARCHAR(255) NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT uk_doctors_email UNIQUE (email)
);

CREATE TABLE doctor_available_slots (
  doctor_id BIGINT NOT NULL,
  slot_time TIME NOT NULL,
  PRIMARY KEY (doctor_id, slot_time),
  CONSTRAINT fk_slots_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

CREATE TABLE doctor_blocked_dates (
  doctor_id BIGINT NOT NULL,
  blocked_date DATE NOT NULL,
  PRIMARY KEY (doctor_id, blocked_date),
  CONSTRAINT fk_blocked_dates_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

CREATE TABLE appointments (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  patient_id BIGINT NOT NULL,
  doctor_id BIGINT NOT NULL,
  appointment_date DATE NOT NULL,
  appointment_time TIME NOT NULL,
  status VARCHAR(24) NOT NULL,
  active_slot TINYINT GENERATED ALWAYS AS (CASE WHEN status = 'CANCELLED' THEN NULL ELSE 1 END) STORED,
  CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
  CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
  CONSTRAINT uk_appointment_active_slot UNIQUE (doctor_id, appointment_date, appointment_time, active_slot),
  CONSTRAINT uk_patient_active_slot UNIQUE (patient_id, appointment_date, appointment_time, active_slot),
  INDEX idx_appointments_patient_date (patient_id, appointment_date),
  INDEX idx_appointments_date (appointment_date)
);

CREATE TABLE medical_history (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  patient_id BIGINT NOT NULL,
  title VARCHAR(120) NOT NULL,
  details VARCHAR(4000) NOT NULL,
  allergies VARCHAR(2000) NULL,
  created_at DATETIME(6) NOT NULL,
  CONSTRAINT fk_history_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
  INDEX idx_history_patient_created (patient_id, created_at)
);

CREATE TABLE waiting_list (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  patient_id BIGINT NOT NULL,
  specialization VARCHAR(100) NOT NULL,
  appointment_date DATE NOT NULL,
  preferred_time TIME NULL,
  status VARCHAR(16) NOT NULL,
  priority_score INT NOT NULL DEFAULT 0,
  reserved_appointment_id BIGINT NULL,
  offered_at DATETIME(6) NULL,
  created_at DATETIME(6) NOT NULL,
  CONSTRAINT fk_waiting_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
  CONSTRAINT fk_waiting_reserved_appointment FOREIGN KEY (reserved_appointment_id) REFERENCES appointments(id),
  INDEX idx_waiting_priority (specialization, status, created_at)
);

CREATE TABLE doctor_reviews (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  patient_id BIGINT NOT NULL,
  doctor_id BIGINT NOT NULL,
  appointment_id BIGINT NOT NULL,
  rating INT NOT NULL,
  comment VARCHAR(2000) NULL,
  created_at DATETIME(6) NOT NULL,
  CONSTRAINT ck_review_rating CHECK (rating BETWEEN 1 AND 5),
  CONSTRAINT uk_review_appointment UNIQUE (appointment_id),
  CONSTRAINT fk_review_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
  CONSTRAINT fk_review_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
  CONSTRAINT fk_review_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id),
  INDEX idx_review_doctor (doctor_id, created_at)
);

CREATE TABLE prescriptions (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  patient_id BIGINT NOT NULL,
  doctor_id BIGINT NOT NULL,
  appointment_id BIGINT NOT NULL,
  medicine_name VARCHAR(160) NOT NULL,
  dosage VARCHAR(160) NOT NULL,
  frequency VARCHAR(160) NOT NULL,
  duration VARCHAR(120) NOT NULL,
  instructions VARCHAR(2000) NULL,
  prescription_date DATE NOT NULL,
  CONSTRAINT fk_prescription_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
  CONSTRAINT fk_prescription_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
  CONSTRAINT fk_prescription_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id),
  INDEX idx_prescription_patient_date (patient_id,prescription_date)
);

CREATE TABLE notifications (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  patient_id BIGINT NOT NULL,
  type VARCHAR(40) NOT NULL,
  message VARCHAR(1000) NOT NULL,
  dedupe_key VARCHAR(190) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  read_at DATETIME(6) NULL,
  CONSTRAINT uk_notification_dedupe UNIQUE (dedupe_key),
  CONSTRAINT fk_notification_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
  INDEX idx_notification_patient_created (patient_id, created_at)
);

CREATE TABLE emergency_requests (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  patient_id BIGINT NOT NULL,
  specialization VARCHAR(100) NOT NULL,
  requested_date DATE NOT NULL,
  reason VARCHAR(2000) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
  clinical_priority INT NOT NULL DEFAULT 0,
  reviewed_by_doctor_id BIGINT NULL,
  clinical_notes VARCHAR(2000) NULL,
  created_at DATETIME(6) NOT NULL,
  reviewed_at DATETIME(6) NULL,
  CONSTRAINT fk_emergency_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
  CONSTRAINT fk_emergency_reviewer FOREIGN KEY (reviewed_by_doctor_id) REFERENCES doctors(id),
  INDEX idx_emergency_review (specialization,status,created_at)
);
