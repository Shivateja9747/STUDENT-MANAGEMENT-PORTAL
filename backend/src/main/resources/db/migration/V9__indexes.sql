-- Non-duplicate performance indexes
-- Note: Indexes that are prefixes of UNIQUE keys (such as student_id on marks or (department_id, semester, section) on timetable_slots) are omitted to avoid redundancy.

CREATE INDEX idx_attendance_date_student ON attendance_records (student_id, attendance_date);
CREATE INDEX idx_notifications_user_read ON notifications (user_id, read_at);
CREATE INDEX idx_audit_logs_occurred_at ON audit_logs (occurred_at);
CREATE INDEX idx_students_dept_sem_sec ON students (department_id, semester, section);
CREATE INDEX idx_teachers_dept ON teachers (department_id);
CREATE INDEX idx_qr_sessions_active ON qr_sessions (is_active, expires_at);
