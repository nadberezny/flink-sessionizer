-- Binary logging needs to be enabled in MySQL configuration
-- log-bin=mysql-bin
-- binlog_format=ROW

-- CDC needs to read the binary log (binlog) to track changes in the MySQL database and a user needs to have privileges to replication.
CREATE USER 'test_user'@'%' IDENTIFIED WITH mysql_native_password BY 'test_password';
GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'test_user'@'%';
GRANT SELECT, RELOAD, SHOW DATABASES, LOCK TABLES ON *.* TO 'test_user'@'%';
FLUSH PRIVILEGES;

CREATE TABLE testdb.order_returns (
  order_id varchar(255) primary key,
  return_timestamp timestamp(6)
);
