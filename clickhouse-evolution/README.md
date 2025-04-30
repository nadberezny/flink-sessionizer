# ClickHouse Evolution

*Application for automated ClickHouse schema evolution.*

## Overview
This repository keeps track of ClickHouse schema evolution. 
It is designed to be used in a CI/CD pipeline to automatically apply schema changes to a ClickHouse cluster. 
Underneath it uses [Liquibase](https://docs.liquibase.com/concepts/introduction-to-liquibase.html) to manage the DDL changes.
Applied changesets are persisted in the `DATABASECHANGELOG` table in the `system` database, 
which is a non-distributed table, and the data is stored on a single shard.

## How to use
In order to add a new schema change ([changeset](https://docs.liquibase.com/concepts/changelogs/changeset.html)), 
you need to create a new file in the `src/main/java/resources/db/changelog` directory or append a changeset to an existing file.
Liquibase provides several formats in which you can represent DDL, 
but we use [liquibase formatted sql](https://docs.liquibase.com/concepts/changelogs/sql-format.html) because of its flexibility. 
All changesets should specify the [context](https://docs.liquibase.com/concepts/changelogs/attributes/contexts.html) of the changeset.
Contexts become helpful when `ClickHouse Evolution` is used in a Test Suite to set up CH database, since it enables
running only the changesets matching a specific context.

## How to use in an existing project
To use `ClickHouse Evolution` with an existing database, you need first to make Liquibase sync Changelog. 
Syncing Changelog means that Liquibase will create a `DATABASECHANGELOG` table 
and fill it with all the changesets that are defined in the Changelog files.
To do this, yon need to deploy `ClickHouse Evolution` with syncChangeLog option enabled.  
*!This option is meant only for initial setup or when there is a need to move liquibase tables into another schema.*




