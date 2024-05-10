-- Project 3: Task 5
-- Create the employee table and load it with data

USE moviedb;

CREATE TABLE IF NOT EXISTS employees (
    email varchar(50) primary key,
    password varchar(20) not null,
    fullname varchar(100)
);

INSERT INTO employees VALUES ('classta@email.edu', 'classta', 'TA CS122B');