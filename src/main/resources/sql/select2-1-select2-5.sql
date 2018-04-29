-- а) Найти имена покупателей, возраст которых от 16 до 35.
SELECT
  name,
  age
FROM customers
WHERE age >= 16 AND age <= 35;

-- б) Найти имена покупателей, фамилия которых оканчивается на 'OV'
SELECT
  name,
  surname
FROM customers
WHERE surname ILIKE '%OV';

-- в) Найти название самого дорогого по закупке товара, в имени которого присутствует буква 'V',
--    но не первая и не последняя.
SELECT
  name,
  purchase_price
FROM products
WHERE name SIMILAR TO '[^Vv]+[Vv]+[^Vv]+'
      AND purchase_price =
          (SELECT max(purchase_price)
           FROM products
           WHERE purchase_price IN (SELECT purchase_price
                                    FROM products
                                    WHERE name SIMILAR TO '[^Vv]+[Vv]+[^Vv]+'));

-- г) Найти имена покупателей, в имени которых присутствует буква 'V' и не больше двух раз
SELECT
  name,
  surname
FROM customers
WHERE name SIMILAR TO '([^Vv]*[Vv][^Vv]*){0,2}';

-- д) Найти имена покупателей, длина имени которых более 3 символов и 4 символ это 'O' (Латинская буква О)
--  и возраст менее 50 лет.
SELECT name
FROM customers
WHERE name ILIKE '___o%' AND age < 50;