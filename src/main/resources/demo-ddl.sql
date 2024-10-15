CREATE TABLE events
(
    created_dt DATETIME     NOT NULL,
    event_type VARCHAR(20)  NOT NULL,
    email      VARCHAR(100) NOT NULL,
    amount     INT,
    PRIMARY KEY (created_dt, event_type, email)
);

CREATE TABLE users
(
    email           VARCHAR(255) NOT NULL,
    current_balance INT,
    PRIMARY KEY (email)
);