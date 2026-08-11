INSERT INTO students (id, school_name, student_name, class_section, profile_photo_url, annual_fee, interest_rate_percent, activated)
SELECT 1, 'Delhi Public School', 'Jessica John Jones', 'FS1 Acacia', 'https://i.pravatar.cc/150?img=47', 340000.00, 0, false
WHERE NOT EXISTS (SELECT 1 FROM students WHERE id = 1);

INSERT INTO students (id, school_name, student_name, class_section, profile_photo_url, annual_fee, interest_rate_percent, activated)
SELECT 2, 'Ryan International School', 'Aditi Sharma', 'FS2 Maple', 'https://i.pravatar.cc/150?img=32', 250000.00, 0, false
WHERE NOT EXISTS (SELECT 1 FROM students WHERE id = 2);

INSERT INTO students (id, school_name, student_name, class_section, profile_photo_url, annual_fee, interest_rate_percent, activated)
SELECT 3, 'DAV Public School', 'Rohan Mehta', 'Grade 5 Oak', 'https://i.pravatar.cc/150?img=12', 180000.00, 0, false
WHERE NOT EXISTS (SELECT 1 FROM students WHERE id = 3);

INSERT INTO students (id, school_name, student_name, class_section, profile_photo_url, annual_fee, interest_rate_percent, activated)
SELECT 4, 'Modern High School', 'Sneha Iyer', 'Grade 8 Birch', 'https://i.pravatar.cc/150?img=45', 425000.00, 0, true
WHERE NOT EXISTS (SELECT 1 FROM students WHERE id = 4);

INSERT INTO students (id, school_name, student_name, class_section, profile_photo_url, annual_fee, interest_rate_percent, activated)
SELECT 5, 'St. Xavier''s School', 'Karan Verma', 'FS3 Cedar', 'https://i.pravatar.cc/150?img=68', 300000.00, 0, false
WHERE NOT EXISTS (SELECT 1 FROM students WHERE id = 5);
