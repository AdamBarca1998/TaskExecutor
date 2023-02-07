DROP TABLE IF EXISTS task_schedule;
DROP TABLE IF EXISTS task;
DROP TABLE IF EXISTS task_config;
DROP TABLE IF EXISTS task_lock;

CREATE TABLE task_lock (
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name            VARCHAR(64) NOT NULL,
    lock_until      TIMESTAMP NOT NULL,
    locked_at       TIMESTAMP NOT NULL,
    locked_by       VARCHAR(255) NOT NULL
);

CREATE TABLE task_config (
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    priority        INT NOT NULL,
    is_heavy        BOOLEAN,
    start_date_time TIMESTAMP
);

CREATE TABLE task (
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    task_config_id  BIGINT NOT NULL REFERENCES task_config(id),
    task_lock_id    BIGINT NOT NULL REFERENCES task_lock(id)
);

CREATE TABLE task_schedule (
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    cron_text       VARCHAR(10),
    fixed_delay     TIME,
    fixed_rate      TIME,
    task_config_id  BIGINT NOT NULL REFERENCES task_config(id)
)