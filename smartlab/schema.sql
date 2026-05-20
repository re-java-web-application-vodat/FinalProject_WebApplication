-- ============================================================
-- SmartLab: Academic Support & Equipment Management Platform
-- Complete Database Schema (MySQL)
-- ============================================================

-- 1. Bảng người dùng hệ thống
CREATE TABLE IF NOT EXISTS users (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email    VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(20)  NOT NULL,   -- STUDENT, LECTURER, ADMIN
    enable   TINYINT(1) DEFAULT 1
);

-- 2. Hồ sơ cá nhân (1-1 với users)
CREATE TABLE IF NOT EXISTS user_profiles (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    fullname VARCHAR(255),
    phone    VARCHAR(20),
    address  VARCHAR(500),
    avatar   VARCHAR(500),
    user_id  BIGINT UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 3. Khoa / Ngành
CREATE TABLE IF NOT EXISTS departments (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- 4. Giảng viên (1-1 với users, N-1 với departments)
CREATE TABLE IF NOT EXISTS lecturers (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    specialization  VARCHAR(255),
    user_id         BIGINT UNIQUE,
    department_id   BIGINT,
    FOREIGN KEY (user_id)       REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- 5. Thiết bị phòng Lab
CREATE TABLE IF NOT EXISTS equipments (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    description        TEXT,
    quantity           INT NOT NULL DEFAULT 0,
    available_quantity INT NOT NULL DEFAULT 0
);

-- 6. Buổi mentoring / tư vấn học thuật
CREATE TABLE IF NOT EXISTS mentoring_sessions (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_date  DATE         NOT NULL,
    start_time    TIME         NOT NULL,
    end_time      TIME         NOT NULL,
    status        VARCHAR(30)  NOT NULL,  -- PENDING, PENDING_PAYMENT, CONFIRMED, COMPLETED, CANCELLED
    created_at    DATETIME     NOT NULL,
    student_id    BIGINT       NOT NULL,
    lecturer_id   BIGINT       NOT NULL,
    department_id BIGINT       NOT NULL,
    FOREIGN KEY (student_id)    REFERENCES users(id),
    FOREIGN KEY (lecturer_id)   REFERENCES lecturers(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- 7. Phiếu mượn thiết bị đơn lẻ (do sinh viên tự tạo)
CREATE TABLE IF NOT EXISTS equipment_borrows (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity              INT          NOT NULL,
    borrow_date           DATE,
    expected_return_date  DATE,
    actual_return_date    DATE,
    status                VARCHAR(20)  NOT NULL,  -- PENDING, EXPORTED, RETURNED
    created_at            DATETIME,
    student_id            BIGINT       NOT NULL,
    equipment_id          BIGINT       NOT NULL,
    FOREIGN KEY (student_id)   REFERENCES users(id),
    FOREIGN KEY (equipment_id) REFERENCES equipments(id)
);

-- 8. Giao dịch thanh toán (1-1 với mentoring_sessions)
CREATE TABLE IF NOT EXISTS payment_transactions (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_code VARCHAR(255),
    amount           DOUBLE,
    payment_method   VARCHAR(50),
    payment_status   VARCHAR(20),
    created_at       DATETIME,
    session_id       BIGINT UNIQUE,
    FOREIGN KEY (session_id) REFERENCES mentoring_sessions(id)
);

-- 9. Đánh giá năng lực sau buổi tư vấn (1-1 với mentoring_sessions)
CREATE TABLE IF NOT EXISTS competency_assessments (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    assessment_content  TEXT,                          -- Nội dung đánh giá chính
    score               DOUBLE,                        -- Điểm năng lực (0.0 - 10.0)
    strengths           TEXT,                          -- Điểm mạnh của sinh viên
    weaknesses          TEXT,                          -- Điểm cần cải thiện
    recommendations     TEXT,                          -- Đề xuất của giảng viên
    created_at          DATETIME NOT NULL,
    session_id          BIGINT   NOT NULL UNIQUE,      -- Mỗi session chỉ có 1 đánh giá
    student_id          BIGINT   NOT NULL,
    lecturer_id         BIGINT   NOT NULL,
    FOREIGN KEY (session_id)  REFERENCES mentoring_sessions(id),
    FOREIGN KEY (student_id)  REFERENCES users(id),
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(id)
);

-- 10. Phiếu mượn thiết bị (do giảng viên tạo sau buổi tư vấn)
CREATE TABLE IF NOT EXISTS borrow_slips (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, RELEASED, RETURNED
    note        TEXT,
    created_at  DATETIME    NOT NULL,
    session_id  BIGINT      NOT NULL,
    student_id  BIGINT      NOT NULL,
    lecturer_id BIGINT      NOT NULL,
    FOREIGN KEY (session_id)  REFERENCES mentoring_sessions(id),
    FOREIGN KEY (student_id)  REFERENCES users(id),
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(id)
);

-- 11. Chi tiết từng dòng thiết bị trong phiếu mượn
CREATE TABLE IF NOT EXISTS borrow_slip_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity        INT    NOT NULL,
    borrow_slip_id  BIGINT NOT NULL,
    equipment_id    BIGINT NOT NULL,
    FOREIGN KEY (borrow_slip_id) REFERENCES borrow_slips(id),
    FOREIGN KEY (equipment_id)   REFERENCES equipments(id)
);
