-- Добавляем новые колонки в таблицу orders
ALTER TABLE orders
ADD COLUMN business_key UUID,
ADD COLUMN contract_id VARCHAR(255),
ADD COLUMN delivery_date TIMESTAMP WITH TIME ZONE,
ADD COLUMN inn VARCHAR(20),
ADD COLUMN account_number VARCHAR(50),
ADD COLUMN total_amount NUMERIC(19,2);

-- Обновляем существующие записи (опционально)
UPDATE orders
SET total_amount = (
    SELECT SUM(oi.quantity * oi.price)
    FROM order_items oi
    WHERE oi.order_id = orders.id
)
WHERE total_amount IS NULL;