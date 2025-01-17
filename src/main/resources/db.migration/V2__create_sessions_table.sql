CREATE TABLE sessions (
                          session_id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          password VARCHAR(255) NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);