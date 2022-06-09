CREATE TABLE users (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    unique(email)
);

CREATE TABLE user_auth_password (
    user_id uuid PRIMARY KEY REFERENCES users(id),
    user_password VARCHAR(255) NOT NULL
);
