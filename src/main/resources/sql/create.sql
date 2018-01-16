CREATE TABLE customers (
  id SERIAL PRIMARY KEY,
  name varchar(255) NOT NULL, 
  surname varchar(255) NOT NULL, 
  age SMALLINT NOT NULL
);
CREATE TABLE orders (
  id SERIAL PRIMARY KEY,
  orders_date timestamp NOT NULL,
  customers_id INT NOT NULL,
  shops_id INT NOT NULL
);
CREATE TABLE orders_products (
  orders_id INT NOT NULL,
  products_id INT NOT NULL,
  selling_price numeric(19, 2) NOT NULL,
  count INT NOT NULL,
  PRIMARY KEY (orders_id, products_id)
);
CREATE TABLE products (
  id SERIAL PRIMARY KEY,
  name varchar(255) NOT NULL,
  purchase_price numeric(19, 2) NOT NULL
);
CREATE TABLE shops (
  id SERIAL PRIMARY KEY,
  name varchar(255) NOT NULL,
  address varchar(255) NOT NULL
);
ALTER TABLE orders ADD CONSTRAINT FK_orders_customers FOREIGN KEY (customers_id) REFERENCES customers (id);
ALTER TABLE orders ADD CONSTRAINT FK_orders_shops FOREIGN KEY (shops_id) REFERENCES shops (id);
ALTER TABLE orders_products ADD CONSTRAINT FK_products_id_orders_products FOREIGN KEY (products_id) REFERENCES products (id);
ALTER TABLE orders_products ADD CONSTRAINT FK_orders_id_orders_products FOREIGN KEY (orders_id) REFERENCES orders (id);