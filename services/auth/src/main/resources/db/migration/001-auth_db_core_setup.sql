--liquibase formated sql

--changeset sspdev:001-create-extension-citext
CREATE EXTENSION IF NOT EXISTS "citext";
--rollback DROP EXTENSION IF EXISTS "citext";

--changeset sspdev:002-create-extension-pgcrypto
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
--rollback DROP EXTENSION IF EXISTS "pgcrypto";

--changeset sspdev:003-create-auth-schema
CREATE SCHEMA IF NOT EXISTS auth;
--rollback DROP SCHEMA IF EXISTS auth;

--changeset sspdev:004-create-auth-table
CREATE TABLE IF NOT EXISTS auth.user_identity
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email         CITEXT UNIQUE,
    phone         VARCHAR(32) UNIQUE,
    password_hash TEXT                              NOT NULL,
    password_algo TEXT             DEFAULT 'bcrypt' NOT NULL,
    CHECK (email IS NOT NULL OR phone IS NOT NULL)
);
--rollback DROP TABLE IF EXISTS auth.user_identity;