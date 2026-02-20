--liquibase formated sql

--changeset sspdev:008-create-user-roles-table
CREATE TABLE IF NOT EXISTS auth.user_roles
(
    user_id UUID   NOT NULL,
    role    CITEXT NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES auth.user_identity (id) ON DELETE CASCADE,
    CONSTRAINT check_role CHECK (role IN ('USER', 'ADMIN'))
);
--rollback DROP TABLE IF EXISTS user_roles;