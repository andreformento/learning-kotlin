CREATE SEQUENCE posts_id_seq;

CREATE TABLE IF NOT EXISTS posts(
id INTEGER NOT NULL PRIMARY KEY DEFAULT nextval('posts_id_seq') ,
title VARCHAR(255) NOT NULL,
content VARCHAR(255) NOT NULL
);

ALTER SEQUENCE posts_id_seq OWNED BY posts.id;

CREATE SEQUENCE comments_id_seq;

CREATE TABLE IF NOT EXISTS comments(
id INTEGER NOT NULL PRIMARY KEY DEFAULT nextval('comments_id_seq') ,
post_id INTEGER REFERENCES posts(id),
content VARCHAR(255) NOT NULL
);

ALTER SEQUENCE comments_id_seq OWNED BY comments.id;

INSERT INTO posts (id, title, content) VALUES
(1, 'post1', 'blah1'),
(2, 'post2', 'blabla2');

INSERT INTO comments (id, post_id, content) VALUES
(1, 1, 'cmt1.1'),
(2, 1, 'cmt1.2'),
(3, 2, 'cmt2.1'),
(4, 2, 'cmt2.2'),
(5, 2, 'cmt2.3');

SELECT pg_catalog.setval(pg_get_serial_sequence('posts', 'id'), (SELECT MAX(id) FROM posts)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('comments', 'id'), (SELECT MAX(id) FROM comments)+1);
