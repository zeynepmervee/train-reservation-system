CREATE TABLE trains
(
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(30)  NOT NULL,
    name       VARCHAR(100) NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_trains_code UNIQUE (code)
);