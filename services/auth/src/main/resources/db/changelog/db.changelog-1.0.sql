--liquibase format sql

--changeset auth_gb:1
CREATE EXTENSION IF NOT EXISTS "citext";
--rollback DROP EXTENSION IF EXISTS "citext"

--changeset auth_gb:2
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
--rollback DROP EXTENSION IF EXISTS "pgcrypto"

--changeset auth_db:3
CREATE SCHEMA IF NOT EXISTS auth;
--rollback DROP SCHEMA IF EXISTS auth

--changeset auth_db:4
CREATE TABLE IF NOT EXISTS auth.user_identity
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email         CITEXT UNIQUE,
    phone         VARCHAR(32) UNIQUE,
    password_hash TEXT                              NOT NULL,
    password_algo TEXT             DEFAULT 'bcrypt' NOT NULL,
    CHECK (email IS NOT NULL OR phone IS NOT NULL)
);
--rollback DROP TABLE IF EXISTS auth.user_identity