-- 1. Create a person
INSERT INTO person (personal_number, first_name, last_name, password)
VALUES ('19850505-1234', 'Anna', 'Andersson', 'password');

-- 3. A workshift BASELINE
-- 8h shift monday
INSERT INTO shift (shift_start, shift_end, break_minutes, type, person_id)
VALUES ('2024-04-01 08:00','2024-04-01 17:00', 60,'BASELINE',1);

-- 3. A workshift ACTUAL
-- 6h shift monday
INSERT INTO shift (shift_start, shift_end, break_minutes, type, person_id)
VALUES ('2024-04-01 08:00','2024-04-01 15:00', 60, 'ACTUAL',1);
