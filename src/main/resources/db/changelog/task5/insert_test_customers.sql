-- Сначала удалить данные из order_items
DELETE FROM order_items;

-- Затем удалить данные из orders
DELETE FROM orders;

-- Удалить данные из products (если нужно пересоздать)
DELETE FROM products;

-- Теперь вставлять в правильном порядке:

-- 1. Products
INSERT INTO products (id, name, article, description, category, price, quantity, last_quantity_change, created_at) VALUES
('0a0b2227-fbc8-46d5-a0b0-c7e0cf75f994', 'Laptop', 'ART001', 'High-performance laptop', 'ELECTRONICS', 999.99, 10.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1b2c3338-abcd-57e6-b1c1-d8e1de86f005', 'Smartphone', 'ART002', 'Latest smartphone model', 'ELECTRONICS', 699.99, 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('2c3d4449-cdef-68f7-c2d2-e9f2ef97a116', 'Headphones', 'ART003', 'Wireless noise-cancelling', 'AUDIO', 199.99, 50.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. Orders
INSERT INTO orders (id, customer_id, status, delivery_address, created_at, updated_at) VALUES
('7294be20-8da2-4cad-bca3-1e0b446f9759', 1, 'CREATED', '123 Main St, New York, NY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('f6487930-40ff-46a2-a3da-3fb10f453dc5', 2, 'CONFIRMED', '456 Oak Ave, Los Angeles, CA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. Order Items (только после того как продукты и заказы созданы)
INSERT INTO order_items (id, order_id, product_id, quantity, price, created_at) VALUES
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', '7294be20-8da2-4cad-bca3-1e0b446f9759', '0a0b2227-fbc8-46d5-a0b0-c7e0cf75f994', 1, 999.99, CURRENT_TIMESTAMP),
('b2c3d4e5-f6a7-8901-bcde-f23456789012', 'f6487930-40ff-46a2-a3da-3fb10f453dc5', '1b2c3338-abcd-57e6-b1c1-d8e1de86f005', 1, 699.99, CURRENT_TIMESTAMP);