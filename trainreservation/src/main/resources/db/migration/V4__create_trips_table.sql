CREATE TABLE trips
(
    id                   BIGSERIAL PRIMARY KEY,
    train_id             BIGINT         NOT NULL,
    departure_station_id BIGINT         NOT NULL,
    arrival_station_id   BIGINT         NOT NULL,
    departure_time       TIMESTAMP WITH TIME ZONE NOT NULL,
    arrival_time         TIMESTAMP WITH TIME ZONE NOT NULL,
    base_price           NUMERIC(10, 2) NOT NULL,
    status               VARCHAR(20)    NOT NULL DEFAULT 'SCHEDULED',
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_trips_train
        FOREIGN KEY (train_id) REFERENCES trains (id),

    CONSTRAINT fk_trips_departure_station
        FOREIGN KEY (departure_station_id) REFERENCES stations (id),

    CONSTRAINT fk_trips_arrival_station
        FOREIGN KEY (arrival_station_id) REFERENCES stations (id),

    CONSTRAINT uk_trips_train_departure_time
        UNIQUE (train_id, departure_time),

    CONSTRAINT chk_trips_different_stations
        CHECK (departure_station_id <> arrival_station_id),

    CONSTRAINT chk_trips_time
        CHECK (arrival_time > departure_time),

    CONSTRAINT chk_trips_price
        CHECK (base_price >= 0),

    CONSTRAINT chk_trips_status
        CHECK (status IN ('SCHEDULED', 'CANCELLED', 'COMPLETED'))
);

CREATE INDEX idx_trips_stations_time
    ON trips (departure_station_id, arrival_station_id, departure_time);