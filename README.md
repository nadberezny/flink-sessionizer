# Flink Sessionizer

A streaming application that processes clickstream data and creates user sessions using Apache Flink. The project
consists of multiple modules including clickstream generation, CDC evolution handling, and attribution services.

## Flink Job

For detailed information about the Flink job implementation, please refer to
the [Flink Job README](flink-job/README.md).

## Attribution Service
Node.js application accepting attribution requests and returning attributed order.
Please refer to the [Attribution Service README](attribution-service/README.md)

## CDC Evolution
A module for applying schema migrations to MySql database. More info in the [README](cdc-evolution/README.md)
