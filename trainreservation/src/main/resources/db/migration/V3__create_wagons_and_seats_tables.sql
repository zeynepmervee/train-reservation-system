CREATE TABLE wagons
(
    id           BIGSERIAL PRIMARY KEY,
    train_id     BIGINT      NOT NULL,
    wagon_number INTEGER     NOT NULL,
    wagon_type   VARCHAR(20) NOT NULL,

    CONSTRAINT fk_wagons_train
        FOREIGN KEY (train_id) REFERENCES trains (id) ON DELETE CASCADE,

    CONSTRAINT uk_wagons_train_number
        UNIQUE (train_id, wagon_number),

    CONSTRAINT chk_wagons_type
        CHECK (wagon_type IN ('ECONOMY', 'BUSINESS')),

    CONSTRAINT chk_wagons_number
        CHECK (wagon_number > 0)
);

CREATE TABLE seats
(
    id          BIGSERIAL PRIMARY KEY,
    wagon_id    BIGINT      NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    is_window   BOOLEAN     NOT NULL,

    CONSTRAINT fk_seats_wagon
        FOREIGN KEY (wagon_id) REFERENCES wagons (id) ON DELETE CASCADE,

    CONSTRAINT uk_seats_wagon_number
        UNIQUE (wagon_id, seat_number)
);

CREATE INDEX idx_wagons_train_id ON wagons (train_id);
CREATE INDEX idx_seats_wagon_id ON seats (wagon_id);