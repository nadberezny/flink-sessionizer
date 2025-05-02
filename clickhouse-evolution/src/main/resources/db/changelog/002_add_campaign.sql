--liquibase formatted sql

--changeset juliusz.nadberezny:002.0 context:demo
ALTER TABLE getindata.sessions
    ADD COLUMN `campaign` String  DEFAULT '' AFTER marketingChannel;
--rollback empty;

--changeset juliusz.nadberezny:002.1 context:demo
ALTER TABLE getindata.orders_attributed
    ADD COLUMN `campaign` String  DEFAULT '' AFTER marketingChannel;
--rollback empty;
