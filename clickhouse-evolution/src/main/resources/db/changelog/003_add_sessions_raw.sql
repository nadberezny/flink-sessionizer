--liquibase formatted sql

--changeset juliusz.nadberezny:003.0 context:demo
CREATE TABLE getindata.sessions_raw
(
    `sessionId`        UUID,
    `userId`           String,
    `marketingChannel` String,
    `timestamp`        DateTime64(3, 'UTC'),
    `pageViewCount`    UInt32,
    `durationMillis`   UInt32
) ENGINE MergeTree()
    ORDER BY (`timestamp`)
      PARTITION BY toYYYYMMDD(`timestamp`);
--rollback DROP TABLE getindata.sessions_raw;
