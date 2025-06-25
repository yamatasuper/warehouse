CREATE TABLE products (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    article VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(255) NOT NULL,
    price NUMERIC(19,2) NOT NULL,
    quantity NUMERIC(19,2) NOT NULL,
    last_quantity_change TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP NOT NULL
    );