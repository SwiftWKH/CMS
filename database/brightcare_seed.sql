-- BrightCare Derby integration seed data.
-- Run after brightcare_schema.sql.
-- Test passwords:
-- admin1 / admin123
-- doc01 / doctor123
-- rec01 / receptionist123
-- pat01 / patient123

DELETE FROM CONSULTATION_NOTE;
DELETE FROM APPOINTMENT;
DELETE FROM DOCTOR;
DELETE FROM PATIENT;
DELETE FROM USER_ACCOUNT;

INSERT INTO USER_ACCOUNT (user_id, username, password_hash, role, status)
VALUES (1, 'admin1', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', 'ACTIVE');

INSERT INTO USER_ACCOUNT (user_id, username, password_hash, role, status)
VALUES (2, 'doc01', 'f348d5628621f3d8f59c8cabda0f8eb0aa7e0514a90be7571020b1336f26c113', 'DOCTOR', 'ACTIVE');

INSERT INTO USER_ACCOUNT (user_id, username, password_hash, role, status)
VALUES (3, 'rec01', 'a27dce9d8b5488238487ca36967563b7487b12232e3d1cb98442360f033cfbd7', 'RECEPTIONIST', 'ACTIVE');

INSERT INTO USER_ACCOUNT (user_id, username, password_hash, role, status)
VALUES (4, 'pat01', 'd4587ea9ead060c13fd994f21ecfa7926272a78854a2c20136b10a3c9e53e71e', 'PATIENT', 'ACTIVE');

INSERT INTO USER_ACCOUNT (user_id, username, password_hash, role, status)
VALUES (5, 'doc02', 'f348d5628621f3d8f59c8cabda0f8eb0aa7e0514a90be7571020b1336f26c113', 'DOCTOR', 'ACTIVE');

INSERT INTO USER_ACCOUNT (user_id, username, password_hash, role, status)
VALUES (6, 'pat02', 'd4587ea9ead060c13fd994f21ecfa7926272a78854a2c20136b10a3c9e53e71e', 'PATIENT', 'ACTIVE');

INSERT INTO PATIENT (patient_id, user_id, first_name, last_name, ic_passport_no, contact_number, medical_record_id)
VALUES (1, 4, 'Alicia', 'Tan', 'P900101-10-1111', '0123456789', 'MR-0001');

INSERT INTO PATIENT (patient_id, user_id, first_name, last_name, ic_passport_no, contact_number, medical_record_id)
VALUES (2, 6, 'Brian', 'Lim', 'P910202-10-2222', '0134567890', 'MR-0002');

INSERT INTO PATIENT (patient_id, user_id, first_name, last_name, ic_passport_no, contact_number, medical_record_id)
VALUES (3, NULL, 'Chloe', 'Ng', 'P920303-10-3333', '0145678901', 'MR-0003');

INSERT INTO DOCTOR (doctor_id, user_id, name, specialization, contact_number)
VALUES (1, 2, 'Dr Lim', 'General Medicine', '0111111111');

INSERT INTO DOCTOR (doctor_id, user_id, name, specialization, contact_number)
VALUES (2, 5, 'Dr Wong', 'Cardiology', '0122222222');

INSERT INTO APPOINTMENT (appointment_id, patient_id, doctor_id, appointment_date, appointment_time, status, reason)
VALUES (1, 1, 1, DATE('2026-08-14'), TIME('09:00:00'), 'BOOKED', 'Fever and cough');

INSERT INTO APPOINTMENT (appointment_id, patient_id, doctor_id, appointment_date, appointment_time, status, reason)
VALUES (2, 2, 1, DATE('2026-08-14'), TIME('10:00:00'), 'BOOKED', 'Follow-up consultation');

INSERT INTO APPOINTMENT (appointment_id, patient_id, doctor_id, appointment_date, appointment_time, status, reason)
VALUES (3, 1, 2, DATE('2026-08-15'), TIME('14:00:00'), 'COMPLETED', 'Chest discomfort');

INSERT INTO APPOINTMENT (appointment_id, patient_id, doctor_id, appointment_date, appointment_time, status, reason)
VALUES (4, 3, 1, DATE('2026-08-16'), TIME('11:00:00'), 'CANCELLED', 'Vaccination');

INSERT INTO CONSULTATION_NOTE (note_id, appointment_id, doctor_id, notes, diagnosis, prescription, created_at)
VALUES (1, 3, 2, 'Patient advised to monitor symptoms and return if pain worsens.', 'Mild chest wall strain', 'Rest and paracetamol when needed', TIMESTAMP('2026-08-15 14:35:00'));
