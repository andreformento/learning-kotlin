CREATE TABLE users (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

INSERT INTO users (id, name, email) VALUES
('6340ac3d-4a68-4a2c-bbf6-5e7bcc4a6d07', 'Eddie', 'iron-maiden@evil.hell'),
('f25a42ca-dd73-4c82-9d9c-b7da6d50d0e9', 'share', 'share@blah.io'),
('0c7c580f-8213-4d3b-bd09-e40a57b634ad', 'user2', 'user@two.dev');
