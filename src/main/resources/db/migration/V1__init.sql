CREATE TABLE member (
    id          SERIAL          PRIMARY KEY,
    name        VARCHAR(50)     NOT NULL UNIQUE
);

CREATE TABLE holiday (
    id          SERIAL          PRIMARY KEY,
    member_id   INT            REFERENCES member(id) NOT NULL,
    description VARCHAR(50)     NOT NULL,
    start_date  DATE            NOT NULL,
    end_date    DATE            NOT NULL
);