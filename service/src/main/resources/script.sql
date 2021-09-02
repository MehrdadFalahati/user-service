CREATE TABLE User
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    last_name  VARCHAR(100),
    first_name VARCHAR(100)
);

CREATE TABLE Branch
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    address     VARCHAR(100),
    status_     BIT,
    branch_name VARCHAR(100)
);