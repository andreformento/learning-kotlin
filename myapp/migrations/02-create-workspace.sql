CREATE TABLE workspace (
    id uuid DEFAULT uuid_generate_v4(),
    description VARCHAR NOT NULL,
    PRIMARY KEY (id)
);
