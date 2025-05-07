--liquibase formatted sql

--changeset juliusz.nadberezny:004.0 context:demo
ALTER TABLE getindata.orders_attributed
    ADD COLUMN `productId` String  DEFAULT '' AFTER weight;
--rollback empty;

--changeset juliusz.nadberezny:004.1 context:demo
ALTER TABLE getindata.orders_attributed
    ADD COLUMN `productName` String  DEFAULT '' AFTER productId;
--rollback empty;

--changeset juliusz.nadberezny:004.2 context:demo
ALTER TABLE getindata.orders_attributed
    ADD COLUMN `productPrice` Decimal32(4)  DEFAULT 0 AFTER productName;
--rollback empty;

--changeset juliusz.nadberezny:004.3 context:demo
ALTER TABLE getindata.orders_attributed
    ADD COLUMN `productQuantity` UInt32  DEFAULT 0 AFTER productPrice;
--rollback empty;
