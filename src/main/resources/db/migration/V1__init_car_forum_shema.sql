-- Създаване на база данни
CREATE DATABASE IF NOT EXISTS carForum;
USE carForum;

-- Създаване на таблица users
CREATE TABLE IF NOT EXISTS users (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    username VARCHAR(32) UNIQUE NOT NULL,
    password VARCHAR(256) NOT NULL,
    email VARCHAR(64) UNIQUE NOT NULL,
    profile_photo VARCHAR(256) DEFAULT '/assets/img/bitAvatar1.png',
    role VARCHAR(32) NOT NULL,
    is_blocked BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (CHAR_LENGTH(first_name) BETWEEN 4 AND 32),
    CHECK (CHAR_LENGTH(last_name) BETWEEN 4 AND 32)
    );

-- Създаване на таблица admin_phones
CREATE TABLE IF NOT EXISTS admin_phones (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            phone_number VARCHAR(20) NOT NULL,
    user_id INT NOT NULL,
    UNIQUE (phone_number),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
    );

-- Създаване на таблица posts
CREATE TABLE IF NOT EXISTS posts (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     user_id INT NOT NULL,
                                     title VARCHAR(64) NOT NULL,
    content VARCHAR(8192) NOT NULL,
    likes INT DEFAULT 0,
    dislikes INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    CHECK (CHAR_LENGTH(title) BETWEEN 16 AND 64),
    CHECK (CHAR_LENGTH(content) BETWEEN 32 AND 8192),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
    );

-- Създаване на таблица comments
CREATE TABLE IF NOT EXISTS comments (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        post_id INT,
                                        user_id INT NOT NULL,
                                        content VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
    );

-- Създаване на таблица tags
CREATE TABLE IF NOT EXISTS tags (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(32) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
    );

-- Създаване на таблица post_tags (много към много връзка между posts и tags)
CREATE TABLE IF NOT EXISTS post_tags (
                                         post_id INT NOT NULL,
                                         tag_id INT NOT NULL,
                                         PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
    );

-- Създаване на таблица posts_users_likes (много към много връзка между posts и users)
CREATE TABLE IF NOT EXISTS posts_users_likes (
                                                 post_id INT NOT NULL,
                                                 user_id INT NOT NULL,
                                                 PRIMARY KEY (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
    );
