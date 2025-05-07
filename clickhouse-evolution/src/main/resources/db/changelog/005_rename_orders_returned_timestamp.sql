--liquibase formatted sql

--changeset juliusz.nadberezny:005.0 context:demo
ALTER TABLE getindata.orders_attributed
    RENAME COLUMN `returnedTimestamp` TO `returnTimestamp`;
--rollback empty;
