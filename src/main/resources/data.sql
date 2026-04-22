-- 1. Create a person
INSERT INTO person (personal_number, first_name, last_name)
VALUES ('19850505-1234', 'Anna', 'Andersson');

-- 2. Add a child, born 10 month ago and the SGI is protected automatically
INSERT INTO child (first_name, birth_date, sgi_protecting, person_id)
VALUES ('Charlie', DATEADD('MONTH', -10, CURRENT_DATE), True, 1);

-- 3. Add a child that is 3 years, SGI is not protected
INSERT INTO child (first_name, birth_date, sgi_protecting, person_id)
VALUES ('Alice', DATEADD('YEAR', -3, CURRENT_DATE), False, 1);

-- 4. Add an employment with current rate 85 % and original rate 100 %
INSERT INTO employment (original_employment_rate, current_employment_rate, original_working_hours, valid_from, valid_to, person_id)
VALUES (100, 85, 34.2, '2024-01-01', '2024-05-01', 1);

-- 5. A workshift
-- 10 h, 20 minutes shift, starts at thursday and ends at friday, mainday friday
INSERT INTO shift (shift_start, shift_end, person_id, employment_id)
VALUES ('2024-04-04 20:50:00','2024-04-05 07:10:00', 1, 1);

-- 6. A workshift
-- 10 h, 20 minutes shift, starts at friday and ends at saturday, mainday saturday
INSERT INTO shift (shift_start, shift_end, person_id, employment_id)
VALUES ('2024-04-05 20:50:00','2024-04-06 07:10:00', 1, 1);

-- 7. A workshift
-- 10 h, 20 minutes shift, starts at saturday and ends at sunday, mainday sunday
INSERT INTO shift (shift_start, shift_end, person_id, employment_id)
VALUES ('2024-04-06 20:50:00','2024-04-07 07:10:00', 1, 1);

-- 8. A workshift that is overlapping a new week
-- 10 h, 20 minutes shift, starts at sunday and ends at monday, mainday monday
INSERT INTO shift (shift_start, shift_end, person_id, employment_id)
VALUES ('2024-04-07 20:50:00','2024-04-08 07:10:00', 1, 1);

-- 10. A workshift that is overlapping from an earlier week, hours should be included
-- 10 h, 20 minutes shift, starts at sunday and ends at monday, mainday monday
INSERT INTO shift (shift_start, shift_end, person_id, employment_id)
VALUES ('2024-03-31 20:50:00','2024-04-01 07:10:00', 1, 1);

-- 11. Parental leave
-- One whole day
INSERT INTO parental_leave (date, extent, person_id)
VALUES ('2024-04-06', 1, 1)
