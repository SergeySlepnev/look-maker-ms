--liquibase formated sql

--changeset sspdev:006-set-user_email_unique_constraint_name
ALTER TABLE auth.user_identity
    ADD CONSTRAINT unique_user_email UNIQUE (email);
--rollback ALTER TABLE auth.user_identity DROP CONSTRAINT unique_user_email;

--changeset sspdev:007-set-user_phone_unique_constraint_name
ALTER TABLE auth.user_identity
    ADD CONSTRAINT unique_user_phone UNIQUE (phone);
--rollback ALTER TABLE auth.user_identity DROP CONSTRAINT unique_user_phone;