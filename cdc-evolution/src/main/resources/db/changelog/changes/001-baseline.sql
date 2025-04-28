--liquibase formatted sql

-- CDC needs to read the binary log (binlog) to track changes in the MySQL database and a user needs to have privileges to replication.
--changeset baseline:000
CREATE USER '${cdc.user}'@'%' IDENTIFIED WITH mysql_native_password BY '${cdc.password}';
GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO '${cdc.user}'@'%';
GRANT SELECT, RELOAD, SHOW DATABASES, LOCK TABLES ON *.* TO '${cdc.user}'@'%';
FLUSH PRIVILEGES;

CREATE DATABASE IF NOT EXISTS ${cdc.database};

CREATE TABLE ${cdc.database}.order_returns (
      order_id varchar(255) primary key,
      return_timestamp timestamp(6)
);
