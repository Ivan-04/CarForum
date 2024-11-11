INSERT INTO roles (name)
VALUES ('admin'),
       ('moderator'),
       ('user');

INSERT INTO users (first_name, last_name, username, password, email, profile_photo, role_id, created_at,  is_blocked, is_active)
VALUES ('John', 'Smith', 'johndoe', 'password123', 'john.doe@example.com', '/assets/img/bitAvatar1.png', 1, CURRENT_DATE, FALSE, TRUE),
       ('Jane', 'Smith', 'janesmith', 'password123', 'jane.smith@example.com', '/assets/img/bitAvatar1.png', 1, CURRENT_DATE, FALSE, TRUE),
       ('Alice', 'Johnson', 'alicejohnson', 'password123', 'alice.johnson@example.com', '/assets/img/bitAvatar1.png', 2, CURRENT_DATE,
        FALSE, TRUE);