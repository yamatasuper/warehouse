#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE warehouse;
    CREATE DATABASE camunda;
    GRANT ALL PRIVILEGES ON DATABASE warehouse TO postgres;
    GRANT ALL PRIVILEGES ON DATABASE camunda TO postgres;
EOSQL