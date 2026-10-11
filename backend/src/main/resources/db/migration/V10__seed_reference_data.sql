-- Seed Grading Scale (non-overlapping ranges covering 0 - 100)
INSERT INTO grading_scale (grade, min_percentage, max_percentage, gpa_point, description) VALUES
('F', 0.0, 39.99, 0.0, 'Fail'),
('D', 40.0, 49.99, 5.0, 'Pass'),
('C', 50.0, 59.99, 6.0, 'Above Average'),
('B', 60.0, 69.99, 7.0, 'Good'),
('A', 70.0, 79.99, 8.0, 'Very Good'),
('A+', 80.0, 89.99, 9.0, 'Excellent'),
('O', 90.0, 100.0, 10.0, 'Outstanding')
ON DUPLICATE KEY UPDATE
    min_percentage=VALUES(min_percentage),
    max_percentage=VALUES(max_percentage),
    gpa_point=VALUES(gpa_point),
    description=VALUES(description);

-- Seed Current Academic Year
INSERT INTO academic_years (label, start_date, end_date, is_current) VALUES
('2024-2025', '2024-08-01', '2025-05-31', TRUE)
ON DUPLICATE KEY UPDATE is_current=VALUES(is_current);

-- Seed Default App Settings
INSERT INTO app_settings (setting_key, setting_value, description) VALUES
('pass_mark_percentage', '40.0', 'Minimum percentage required to pass a subject'),
('default_geofence_radius_meters', '100.0', 'Default radius for QR code geofencing')
ON DUPLICATE KEY UPDATE setting_value=VALUES(setting_value);
