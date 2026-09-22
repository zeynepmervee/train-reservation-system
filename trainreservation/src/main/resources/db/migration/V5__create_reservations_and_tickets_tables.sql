CREATE TABLE reservations
(
    id          BIGSERIAL PRIMARY KEY,
    trip_id     BIGINT         NOT NULL,
    status      VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    total_price NUMERIC(10, 2) NOT NULL,
    expires_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    version     BIGINT         NOT NULL DEFAULT 0,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reservations_trip
        FOREIGN KEY (trip_id) REFERENCES trips (id),

    CONSTRAINT chk_reservations_status
        CHECK (status IN (
                          'PENDING',
                          'COMPLETED',
                          'CANCELLED',
                          'EXPIRED'
            )),

    CONSTRAINT chk_reservations_total_price
        CHECK (total_price >= 0)
);

CREATE TABLE tickets
(
    id               BIGSERIAL PRIMARY KEY,
    reservation_id   BIGINT         NOT NULL,
    trip_id          BIGINT         NOT NULL,
    seat_id          BIGINT         NOT NULL,
    first_name       VARCHAR(100)   NOT NULL,
    last_name        VARCHAR(100)   NOT NULL,
    birth_date       DATE           NOT NULL,
    gender            VARCHAR(10)    NOT NULL,
    passenger_type   VARCHAR(20)    NOT NULL,
    ticket_price     NUMERIC(10, 2) NOT NULL,
    status           VARCHAR(20)    NOT NULL DEFAULT 'HELD',
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tickets_reservation
        FOREIGN KEY (reservation_id)
            REFERENCES reservations (id) ON DELETE CASCADE,

    CONSTRAINT fk_tickets_trip
        FOREIGN KEY (trip_id) REFERENCES trips (id),

    CONSTRAINT fk_tickets_seat
        FOREIGN KEY (seat_id) REFERENCES seats (id),

    CONSTRAINT chk_tickets_gender
        CHECK (gender IN ('FEMALE', 'MALE')),

    CONSTRAINT chk_tickets_passenger_type
        CHECK (passenger_type IN (
                                  'STANDARD',
                                  'STUDENT',
                                  'SENIOR'
            )),

    CONSTRAINT chk_tickets_status
        CHECK (status IN (
                          'HELD',
                          'CONFIRMED',
                          'CANCELLED',
                          'EXPIRED'
            )),

    CONSTRAINT chk_tickets_price
        CHECK (ticket_price >= 0)
);

CREATE UNIQUE INDEX uk_active_tickets_trip_seat
    ON tickets (trip_id, seat_id)
    WHERE status IN ('HELD', 'CONFIRMED');

CREATE INDEX idx_reservations_trip_id
    ON reservations (trip_id);

CREATE INDEX idx_tickets_reservation_id
    ON tickets (reservation_id);

CREATE INDEX idx_tickets_trip_id
    ON tickets (trip_id);