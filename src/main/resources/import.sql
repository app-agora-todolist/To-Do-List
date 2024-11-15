-- 테이블 생성 (테이블이 없다면)
CREATE TABLE IF NOT EXISTS scheduledomain (
     email VARCHAR(255) NOT NULL,
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    schedule DATE NOT NULL,
    reminderDate DATE,
    completionStatus BOOLEAN NOT NULL
    );

-- 데이터 삽입
INSERT INTO scheduledomain (email, title, category, schedule, reminderDate, completionStatus)
VALUES ('tlgud119@naver.com', '제목', '게임', '2024-11-13', '2024-11-13', false);
