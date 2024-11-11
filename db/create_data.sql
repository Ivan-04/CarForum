create database carForum;
use carForum;

# CREATE TABLE roles
# (
#     id   INT AUTO_INCREMENT PRIMARY KEY,
#     name VARCHAR(32) UNIQUE NOT NULL
# );

CREATE TABLE users
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(32)        NOT NULL,
    last_name     VARCHAR(32)        NOT NULL,
    username      VARCHAR(32) UNIQUE NOT NULL,
    password      VARCHAR(256)       NOT NULL,
    email         VARCHAR(64) UNIQUE NOT NULL,
    profile_photo VARCHAR(256) DEFAULT '/assets/img/bitAvatar1.png',
    role   VARCHAR(32)                NOT NULL,
    is_blocked    BOOLEAN      DEFAULT FALSE,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CHECK (CHAR_LENGTH(first_name) BETWEEN 4 AND 32),
    CHECK (CHAR_LENGTH(last_name) BETWEEN 4 AND 32),
    is_active boolean
);

create table admin_phones
(
    id           int auto_increment
        primary key,
    phone_number varchar(20) not null,
    user_id      int         not null,
    constraint phone_number_unique
        unique (phone_number),
    constraint phone_numbers_ibfk_1
        foreign key (user_id) references users (id)
            on delete cascade
);

CREATE TABLE posts
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT           NOT NULL,
    title      VARCHAR(64)   NOT NULL,
    content    VARCHAR(8192) NOT NULL,
    likes      INT       DEFAULT 0,
    dislikes   INT       DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active boolean,
    CHECK (CHAR_LENGTH(posts.title) BETWEEN 16 AND 64),
    CHECK (CHAR_LENGTH(posts.content) BETWEEN 32 AND 8192),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE comments
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    post_id    INT,
    user_id    INT          NOT NULL,
    content    VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active boolean,
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE tags
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(32) UNIQUE NOT NULL,
    is_active boolean
);

CREATE TABLE post_tags
(
    post_id INT NOT NULL,
    tag_id  INT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE ,
    FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
);

CREATE TABLE posts_users_likes
(
    post_id    INT NOT NULL,
    user_id     INT NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE ,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);