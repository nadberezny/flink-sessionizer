--liquibase formatted sql

--changeset juliusz.nadberezny:000.0 context:demo
CREATE DATABASE getindata;
--rollback DROP DATABASE getindata;

--changeset juliusz.nadberezny:000.1 context:demo
CREATE TABLE getindata.sessions
(
    `sessionId`        UUID,
    `userId`           String,
    `marketingChannel` String,
    `timestamp`        DateTime64(3, 'UTC'),
    `pageViewCount`    UInt32,
    `durationMillis`   UInt32
) ENGINE SummingMergeTree((`pageViewCount`, `durationMillis`))
      PRIMARY KEY (sessionId)
      PARTITION BY toYYYYMMDD(`timestamp`);
--rollback DROP TABLE getindata.sessions;

--changeset juliusz.nadberezny:000.2 context:demo
CREATE TABLE getindata.orders_attributed
(
    `orderId`          String,
    `sessionId`        UUID,
    `userId`           String,
    `marketingChannel` String,
    `timestamp`        DateTime64(3, 'UTC'),
    `pageViewCount`    UInt32,
    `durationMillis`   UInt32,
    `total`            Decimal32(4),
    `shipping`         Decimal32(4),
    `weight`           UInt32
) ENGINE ReplacingMergeTree()
      PRIMARY KEY (orderId, sessionId)
      PARTITION BY toYYYYMMDD(`timestamp`);
--rollback DROP TABLE getindata.orders_attributed;
