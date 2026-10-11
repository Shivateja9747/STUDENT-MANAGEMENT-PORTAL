CREATE TABLE IF NOT EXISTS qr_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    section VARCHAR(10) NOT NULL,
    token VARCHAR(100) NOT NULL UNIQUE,
    generated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    expires_at DATETIME(6) NOT NULL,
    closed_at DATETIME(6) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    geofence_lat DOUBLE NULL,
    geofence_lng DOUBLE NULL,
    geofence_radius DOUBLE NULL,
    CONSTRAINT fk_qrs_teacher FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    CONSTRAINT fk_qrs_subject FOREIGN KEY (subject_id) REFERENCES subjects(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS qr_checkins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    qr_session_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    checked_in_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    outside_geofence BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_qrc_session FOREIGN KEY (qr_session_id) REFERENCES qr_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_qrc_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT uq_qrc_session_student UNIQUE (qr_session_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS attendance_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    period INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    marked_by BIGINT NULL,
    source VARCHAR(20) NOT NULL DEFAULT 'MANUAL',
    qr_session_id BIGINT NULL,
    remark VARCHAR(255) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_att_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_att_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT fk_att_marked_by FOREIGN KEY (marked_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_att_qr_session FOREIGN KEY (qr_session_id) REFERENCES qr_sessions(id) ON DELETE SET NULL,
    CONSTRAINT uq_att_student_subj_date_period UNIQUE (student_id, subject_id, attendance_date, period),
    CONSTRAINT chk_att_source CHECK (source IN ('MANUAL', 'QR', 'LEAVE', 'CORRECTION'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS attendance_corrections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attendance_record_id BIGINT NOT NULL,
    requested_by BIGINT NOT NULL,
    requested_status VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reason TEXT NOT NULL,
    reviewed_by BIGINT NULL,
    reviewed_at DATETIME(6) NULL,
    reviewer_comment TEXT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_att_corr_record FOREIGN KEY (attendance_record_id) REFERENCES attendance_records(id) ON DELETE CASCADE,
    CONSTRAINT fk_att_corr_req_by FOREIGN KEY (requested_by) REFERENCES users(id),
    CONSTRAINT fk_att_corr_rev_by FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
