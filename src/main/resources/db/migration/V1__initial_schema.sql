create table expense (
    id BIGSERIAL primary key,
    date date default current_date,
    category VARCHAR(50),
    amount NUMERIC(10, 2) NOT NULL,
    description VARCHAR(255)
)