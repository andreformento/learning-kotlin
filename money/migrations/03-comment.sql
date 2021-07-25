CREATE TABLE comment (
    id uuid DEFAULT uuid_generate_v4(),
    post_id uuid REFERENCES post(id),
    content VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO comment (id, content, post_id) VALUES
('8ebd5ecf-f9c4-4b8c-a34b-631a9f1f7e0a', 'First comment', '6340ac3d-4a68-4a2c-bbf6-5e7bcc4a6d07'),
('5e088e38-179a-4ebf-874a-987bab77692c', 'Second comment', '6340ac3d-4a68-4a2c-bbf6-5e7bcc4a6d07'),
('6a3937e7-6d84-4213-b95e-4a948a99c15b', 'Comment 2.1', 'f25a42ca-dd73-4c82-9d9c-b7da6d50d0e9'),
('8eca344b-2599-4bfa-bbcb-cf0d5481bd1f', 'Comment 2.2', 'f25a42ca-dd73-4c82-9d9c-b7da6d50d0e9');
