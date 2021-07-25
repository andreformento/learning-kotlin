CREATE TABLE post (
    id uuid DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    content VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO post (id, title, content) VALUES
('6340ac3d-4a68-4a2c-bbf6-5e7bcc4a6d07', 'post1', 'blah1'),
('f25a42ca-dd73-4c82-9d9c-b7da6d50d0e9', 'post2', 'blabla2');
