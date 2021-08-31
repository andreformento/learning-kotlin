CREATE TABLE organization (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL
);

INSERT INTO organization (id, name, description) VALUES
('ae0d9647-e235-481f-baaf-818ad50ba8d8', 'shared-organization', 'Shared organization'),
('ace3717e-f66a-484d-b839-3906016a6bf3', 'single-organization', 'Single organization');
