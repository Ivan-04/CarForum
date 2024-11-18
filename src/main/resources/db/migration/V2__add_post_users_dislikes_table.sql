USE carForum;
CREATE TABLE IF NOT EXISTS post_users_dislikes (
                                                   post_id INT NOT NULL,
                                                   user_id INT NOT NULL,
                                                   PRIMARY KEY (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
    );