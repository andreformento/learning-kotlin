CREATE TYPE role_description AS ENUM ('owner', 'admin');

CREATE TABLE organization_role (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id uuid REFERENCES users(id),
    organization_id uuid REFERENCES organization(id),
    organization_role role_description NOT NULL,
    unique(user_id, organization_id)
);

INSERT INTO organization_role (id, user_id, organization_id, organization_role) VALUES
('8262155e-db3f-4619-9d3f-c75296cfa2cb', '6340ac3d-4a68-4a2c-bbf6-5e7bcc4a6d07', 'ae0d9647-e235-481f-baaf-818ad50ba8d8', 'owner'),
('b9f2ec52-9f2f-4510-b527-92d8d4ffde06', 'f25a42ca-dd73-4c82-9d9c-b7da6d50d0e9', 'ae0d9647-e235-481f-baaf-818ad50ba8d8', 'admin'),
('e221f063-41fc-4cc4-90da-2c07d17b78b4', '0c7c580f-8213-4d3b-bd09-e40a57b634ad', 'ace3717e-f66a-484d-b839-3906016a6bf3', 'owner');
