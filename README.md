# Streaming Demo Apps

A monorepo for applications used in [streaming-demo-platform](https://github.com/getindata/streaming-demo-platform). The project
consists of multiple modules including flink job, clickstream generation, db schema evolution handling, and attribution services.

## Flink Job

For detailed information about the Flink job implementation, please refer to
the [Flink Job README](flink-job/README.md).

## Attribution Service
Node.js application accepting attribution requests and returning attributed order.
Please refer to the [Attribution Service README](attribution-service/README.md)

## CDC Evolution
A module for applying schema migrations to MySql database. More info in the [README](cdc-evolution/README.md)

## ClickHouse Evolution
A module for applying schema migrations to ClickHouse database. More info in the [README](clickhouse-evolution/README.md)

## Clickstream generator
A module for generating clickstream. More info in the [README](clickstream-generator/README.md)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

