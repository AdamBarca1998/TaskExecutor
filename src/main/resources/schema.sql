DROP TABLE IF EXISTS task_log;
DROP TABLE IF EXISTS schedule_task;
DROP TABLE IF EXISTS daemon_task;
DROP TABLE IF EXISTS queue_task;
DROP TABLE IF EXISTS task_lock;
DROP SEQUENCE IF EXISTS schedule_task_id_seq;
DROP SEQUENCE IF EXISTS daemon_task_id_seq;
DROP SEQUENCE IF EXISTS task_lock_id_seq;
DROP SEQUENCE IF EXISTS queue_task_id_seq;
DROP SEQUENCE IF EXISTS task_log_id_seq;

CREATE SEQUENCE schedule_task_id_seq
INCREMENT 1
START 1;

CREATE SEQUENCE task_lock_id_seq
INCREMENT 1
START 1;

CREATE SEQUENCE queue_task_id_seq
INCREMENT 1
START 1;

CREATE SEQUENCE daemon_task_id_seq
INCREMENT 1
START 1;

CREATE SEQUENCE task_log_id_seq
INCREMENT 1
START 1;

CREATE TABLE task_lock (
    id  BIGINT PRIMARY KEY DEFAULT nextval('task_lock_id_seq'),
    name    VARCHAR(1024) NOT NULL UNIQUE,
    lock_until  TIMESTAMP WITH TIME ZONE NOT NULL,
    locked_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    locked_by   VARCHAR(256) NOT NULL,
    cluster_name    VARCHAR(256) NOT NULL
);

CREATE TABLE schedule_task (
    id  BIGINT  PRIMARY KEY DEFAULT nextval('schedule_task_id_seq'),
    clazz_path   VARCHAR(1024) NOT NULL,
    enable  BOOLEAN NOT NULL DEFAULT TRUE,
    task_lock_id    BIGINT NOT NULL REFERENCES task_lock(id),

    -- context
    last_execution TIMESTAMP WITH TIME ZONE,
    last_completion TIMESTAMP WITH TIME ZONE,
    next_execution TIMESTAMP WITH TIME ZONE NOT NULL,

    UNIQUE (clazz_path, task_lock_id)
);

CREATE TABLE daemon_task (
    id  BIGINT  PRIMARY KEY DEFAULT nextval('daemon_task_id_seq'),
    clazz_path   VARCHAR(1024) NOT NULL UNIQUE,
    enable  BOOLEAN NOT NULL DEFAULT TRUE,
    task_lock_id    BIGINT NOT NULL REFERENCES task_lock(id),

    -- context
    last_execution TIMESTAMP WITH TIME ZONE,
    last_completion TIMESTAMP WITH TIME ZONE,
    next_execution TIMESTAMP WITH TIME ZONE NOT NULL,

    UNIQUE (clazz_path, task_lock_id)
);

CREATE TABLE queue_task (
    id BIGINT PRIMARY KEY DEFAULT nextval('queue_task_id_seq'),
    clazz TEXT NOT NULL,
    state VARCHAR(16) NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(1024) NOT NULL, --
    owned_by    VARCHAR(1024) NOT NULL, --
    number_of_trials    SMALLINT NOT NULL DEFAULT 2,
    task_lock_id    BIGINT REFERENCES task_lock(id)
);

CREATE TABLE task_log (
    id BIGINT PRIMARY KEY DEFAULT nextval('task_log_id_seq'),
    start_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    finish_date_time TIMESTAMP WITH TIME ZONE,
    state VARCHAR(16) NOT NULL,
    result TEXT NOT NULL,

    queue_task_id BIGINT REFERENCES queue_task(id),
    schedule_task_id BIGINT REFERENCES schedule_task(id),
    daemon_task_id BIGINT REFERENCES daemon_task(id),

    CONSTRAINT check_only_one_task CHECK (NUM_NONNULLS(queue_task_id, schedule_task_id, daemon_task_id) = 1)
)