CREATE TABLE IF NOT EXISTS accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    document_number VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS operations_types (
    operation_type_id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    description VARCHAR(100) NOT NULL UNIQUE,
    allow_negative boolean not null,
    allow_positive boolean not null
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    account_id INT(11),
    operation_type_id INT,
    amount DECIMAL(10 , 2 ),
    event_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_id FOREIGN KEY (account_id)
    REFERENCES accounts (account_id),
    CONSTRAINT fk_operation_type_id FOREIGN KEY (operation_type_id)
    REFERENCES operations_types (operation_type_id)
);

insert into operations_types(operation_type_id, description, allow_negative, allow_positive) value(1, 'COMPRA A VISTA', true, false);
insert into operations_types(operation_type_id, description, allow_negative, allow_positive) value(2, 'COMPRA PARCELADA', true, false);
insert into operations_types(operation_type_id, description, allow_negative, allow_positive) value(3, 'SAQUE', true, false);
insert into operations_types(operation_type_id, description, allow_negative, allow_positive) value(4, 'PAGAMENTO', false, true);
