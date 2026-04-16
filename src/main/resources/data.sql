-- 1. Create a person
INSERT INTO person (personal_number, first_name, last_name)
VALUES ('19850505-1234', 'Anna', 'Andersson');

-- 2. Add a child, born 10 month ago and the SGI is protected automatically
INSERT INTO child (first_name, birth_date, sgi_protecting, person_id)
VALUES ('Charlie', DATEADD('MONTH', -10, CURRENT_DATE), True, 1);

-- 3. Add a child that is 3 years, SGI is not protected
INSERT INTO child (first_name, birth_date, sgi_protecting, person_id)
VALUES ('Alice', DATEADD('YEAR', -3, CURRENT_DATE), False, 1);

-- 4. Add an employment with current rate 80 % and original rate 100 %
INSERT INTO employment (original_employment_rate, current_employment_rate, original_working_hours, valid_from, valid_to, person_id)
VALUES (100, 80, 34.2, '2024-01-01', '2024-05-01', 1);

-- 5. Some work shifts
-- Pass 1:
INSERT INTO shift (shift_start, shift_end, person_id, employment_id)
VALUES ('2022-04-06 15:30:00','2022-04-07 07:10:00', 1, 1);