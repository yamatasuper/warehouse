INSERT INTO products (id, name, article, description, category, price, quantity, last_quantity_change, created_at)
SELECT
    RANDOM_UUID(),
    CASE
        WHEN x % 10 = 0 THEN 'Смартфон Samsung Galaxy S' || (23 + (x % 5))
        WHEN x % 10 = 1 THEN 'Ноутбук Apple MacBook Pro ' || (13 + (x % 4)) || '"'
        WHEN x % 10 = 2 THEN 'Кофеварка DeLonghi Magnifica ' || CHAR(65 + (x % 5))
        WHEN x % 10 = 3 THEN 'Телевизор LG OLED ' || (55 + (x % 6)) || 'C2'
        WHEN x % 10 = 4 THEN 'Наушники Sony WH-1000XM' || (4 + (x % 3))
        WHEN x % 10 = 5 THEN 'Планшет iPad Pro ' || (11 + (x % 2)) || '"'
        WHEN x % 10 = 6 THEN 'Фитнес-браслет Xiaomi Mi Band ' || (6 + (x % 3))
        WHEN x % 10 = 7 THEN 'Микроволновка Panasonic NN-GD' || (37 + (x % 5)) || 'S'
        WHEN x % 10 = 8 THEN 'Робот-пылесос iRobot Roomba ' || (800 + (x % 100))
        ELSE 'Умная колонка Яндекс Станция ' || (1 + (x % 3))
    END,
    CASE
        WHEN x % 10 = 0 THEN 'SM-S' || (911 + x) || 'BZKDSEK'
        WHEN x % 10 = 1 THEN 'MK' || (183 + x) || 'LL/A'
        WHEN x % 10 = 2 THEN 'ECAM' || (22 + (x % 10)) || '.' || (110 + x) || '.B'
        ELSE 'ART-' || x || '-' || SUBSTRING(CAST(RAND()*1000000 AS VARCHAR), 1, 6)
    END,
    CASE
        WHEN x % 10 = 0 THEN 'Флагманский смартфон с динамическим AMOLED 2X экраном ' || (6.1 + (x % 10)/10) || '"'
        WHEN x % 10 = 1 THEN 'Профессиональный ноутбук с процессором M' || (2 + (x % 3)) || ' Pro'
        WHEN x % 10 = 2 THEN 'Автоматическая кофеварка с капучинатором, модель ' || (2020 + (x % 5))
        ELSE 'Высококачественный продукт с уникальными характеристиками, версия ' || x
    END,
    CASE x % 7
        WHEN 0 THEN 'ELECTRONICS'
        WHEN 1 THEN 'CLOTHING'
        WHEN 2 THEN 'FOOD'
        WHEN 3 THEN 'FURNITURE'
        WHEN 4 THEN 'FURNITURE'
        WHEN 5 THEN 'FURNITURE'
        ELSE 'OTHER'
    END,
    (100 + (x % 1000) * 5 + (x % 100)),
    (1 + (x % 100)),
    DATEADD('DAY', -(x % 30), CURRENT_TIMESTAMP),
    DATEADD('DAY', -(x % 365), CURRENT_TIMESTAMP)
FROM SYSTEM_RANGE(1, 1000) AS x;