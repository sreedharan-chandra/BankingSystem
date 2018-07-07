CREATE TABLE Account (
    id bigint IDENTITY(1,1) PRIMARY KEY,
    name varchar(128) NOT NULL,
    balance decimal(18, 0),
    secret ntext

);