-- 1. Skapa en testperson (Föräldern)
INSERT INTO person (personal_number, first_name, last_name)
VALUES ('19850505-1234', 'Anna', 'Andersson');

-- 2. Lägg till ett barn (Föddes för 10 månader sen - SGI är helt skyddad < 1 år)
INSERT INTO child (first_name, birth_date, sgi_protecting, person_id)
VALUES ('Charlie', DATEADD('MONTH', -10, CURRENT_DATE), True, 1);

-- 3. Lägg till ett äldre barn (Föddes för 3 år sen - Kräver sysselsättning för skydd)
INSERT INTO child (first_name, birth_date, sgi_protecting, person_id)
VALUES ('Alice', DATEADD('YEAR', -3, CURRENT_DATE), False, 1);

-- 4. Arbetets omfattning (Original 100%, jobbar nu 80%)
INSERT INTO work_condition (original_employment_rate, current_employment_rate, original_working_hours, valid_from, valid_to, person_id)
VALUES (100, 80, 34.2,DATEADD('YEAR', -1, CURRENT_DATE), DATEADD('YEAR', 1, CURRENT_DATE), 1);

-- 5. Några arbetspass (Shifts) för den senaste veckan
-- Pass 1:
INSERT INTO shift (shift_start, shift_end, person_id, work_condition_id)
VALUES ('2022-04-06 15:30:00','2022-04-07 07:10:00', 1, 1);

-- -- Pass 2: Tisdag 08:00 - 16:30
-- INSERT INTO shift (shift_start, shift_end, person_id)
-- VALUES (TIMESTAMPADD('HOUR', 8, DATEADD('DAY', -6, CURRENT_DATE)),
--         TIMESTAMPADD('MINUTE', 510, DATEADD('DAY', -6, CURRENT_DATE)), 1);
--
-- -- Pass 3: Onsdag 08:00 - 12:00 (Kort dag)
-- INSERT INTO shift (shift_start, shift_end, person_id)
-- VALUES (TIMESTAMPADD('HOUR', 8, DATEADD('DAY', -5, CURRENT_DATE)),
--         TIMESTAMPADD('HOUR', 12, DATEADD('DAY', -5, CURRENT_DATE)), 1);