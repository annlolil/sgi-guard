-- 1. Create a person
INSERT INTO person (personal_number, first_name, last_name)
VALUES ('19850505-1234', 'Anna', 'Andersson');

-- 2. Add an employment with current rate 85 % and original rate 100 %
INSERT INTO employment (original_employment_rate, current_employment_rate, original_working_hours, valid_from, valid_to, person_id)
VALUES (100, 85, 34.2, '2024-01-01', '2024-05-01', 1);

-- 3. A workshift BASELINE
-- 8h shift monday
INSERT INTO shift (shift_start, shift_end, break_minutes, type, person_id, employment_id)
VALUES ('2024-04-01 08:00','2024-04-01 17:00', 60,'BASELINE',1, 1);

-- 3. A workshift ACTUAL
-- 6h shift monday
INSERT INTO shift (shift_start, shift_end, break_minutes, type, person_id, employment_id)
VALUES ('2024-04-01 08:00','2024-04-01 15:00', 60, 'ACTUAL',1, 1);


-- 6. A workshift
-- 10 h, 20 minutes shift, starts at friday and ends at saturday, mainday saturday
-- INSERT INTO shift (shift_start, shift_end, person_id, employment_id)
-- VALUES ('2024-04-05 20:50:00','2024-04-06 07:10:00', 1, 1);

-- -- 7. A workshift
-- -- 10 h, 20 minutes shift, starts at saturday and ends at sunday, mainday sunday
-- INSERT INTO shift (shift_start, shift_end, person_id, employment_id)
-- VALUES ('2024-04-06 20:50:00','2024-04-07 07:10:00', 1, 1);
--
-- -- 8. A workshift that is overlapping a new week
-- -- 10 h, 20 minutes shift, starts at sunday and ends at monday, mainday monday
-- INSERT INTO shift (shift_start, shift_end, person_id, employment_id)
-- VALUES ('2024-04-07 20:50:00','2024-04-08 07:10:00', 1, 1);
--
-- -- 10. A workshift that is overlapping from an earlier week, hours should be included
-- -- 10 h, 20 minutes shift, starts at sunday and ends at monday, mainday monday
-- INSERT INTO shift (shift_start, shift_end, person_id, employment_id)
-- VALUES ('2024-03-31 20:50:00','2024-04-01 07:10:00', 1, 1);

-- 11. Parental leave
-- One whole day
INSERT INTO parental_leave (date, extent, person_id)
VALUES ('2024-04-01', 0.5, 1)
