CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS tools (
    user_id INT NOT NULL,
    tool_id INT AUTO_INCREMENT PRIMARY KEY,
    tool_name VARCHAR(100) NOT NULL,
    tool_type VARCHAR(200) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
    );

ALTER TABLE tools
    ADD CONSTRAINT fk_user_id
        FOREIGN KEY (user_id)
            REFERENCES users(user_id);


INSERT INTO users (name, email, date_of_birth, created_at)
VALUES
    ('Alice Johnson', 'alice@example.com', '1990-01-01', CURRENT_TIMESTAMP),
    ('Bob Smith', 'bob@example.com', '1985-05-12', CURRENT_TIMESTAMP),
    ('Charlie Brown', 'charlie@example.com', '1992-03-10', CURRENT_TIMESTAMP),
    ('Diana Clarke', 'diana@example.com', '1988-07-22', CURRENT_TIMESTAMP);

INSERT INTO tools (user_id, tool_name, tool_type, created_at)
VALUES
    (1, 'Hammer', 'Hand Tool', '2024-01-10 08:23:15'),
    (1, 'Cordless Drill', 'Power Tool', '2024-01-12 14:47:32'),
    (2, 'Circular Saw', 'Power Tool', '2024-01-15 09:12:08'),
    (3, 'Measuring Tape', 'Hand Tool', '2024-01-20 16:35:44'),
    (4, 'Screwdriver Set', 'Hand Tool', '2024-01-25 11:58:27'),
    (2, 'Angle Grinder', 'Power Tool', '2024-01-30 13:22:51');
