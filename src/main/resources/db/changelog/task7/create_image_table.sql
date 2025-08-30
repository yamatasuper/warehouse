-- Create product_image table
CREATE TABLE product_image (
    id UUID PRIMARY KEY NOT NULL,
    product_id UUID NOT NULL,  -- ИЗМЕНИТЬ С BIGINT НА UUID
    s3_key VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Add foreign key constraint
ALTER TABLE product_image
ADD CONSTRAINT fk_product_image_product
FOREIGN KEY (product_id)
REFERENCES products(id);