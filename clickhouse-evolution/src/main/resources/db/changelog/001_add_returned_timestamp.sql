--liquibase formatted sql

--changeset juliusz.nadberezny:001.0 context:demo
ALTER TABLE getindata.orders_attributed
ADD COLUMN `returnedTimestamp` Nullable(DateTime64(3, 'UTC')) DEFAULT null
--rollback empty;
