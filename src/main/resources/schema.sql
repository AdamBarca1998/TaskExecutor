DROP TABLE IF EXISTS schedule_task;
DROP TABLE IF EXISTS daemon_task;
DROP TABLE IF EXISTS queue_task;
DROP TABLE IF EXISTS task_lock;
DROP TABLE IF EXISTS task_context;
DROP SEQUENCE IF EXISTS schedule_task_id_seq;
DROP SEQUENCE IF EXISTS daemon_task_id_seq;
DROP SEQUENCE IF EXISTS task_lock_id_seq;
DROP SEQUENCE IF EXISTS queue_task_id_seq;
DROP SEQUENCE IF EXISTS task_context_id_seq;

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

CREATE SEQUENCE task_context_id_seq
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

CREATE TABLE task_context (
    id BIGINT PRIMARY KEY DEFAULT nextval('task_context_id_seq'),
    start_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    last_execution TIMESTAMP WITH TIME ZONE NOT NULL,
    last_completion TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE schedule_task (
    id  BIGINT  PRIMARY KEY DEFAULT nextval('schedule_task_id_seq'),
    clazz_path   VARCHAR(1024) NOT NULL,
    enable  BOOLEAN NOT NULL DEFAULT TRUE,
    task_lock_id    BIGINT NOT NULL REFERENCES task_lock(id),
    task_context_id BIGINT NOT NULL REFERENCES task_context(id),

    UNIQUE (clazz_path, task_lock_id)
);

CREATE TABLE daemon_task (
    id  BIGINT  PRIMARY KEY DEFAULT nextval('daemon_task_id_seq'),
    clazz_path   VARCHAR(1024) NOT NULL UNIQUE,
    enable  BOOLEAN NOT NULL DEFAULT TRUE,
    task_lock_id    BIGINT NOT NULL REFERENCES task_lock(id),
    task_context_id BIGINT NOT NULL REFERENCES task_context(id),

    UNIQUE (clazz_path, task_lock_id)
);

CREATE TABLE queue_task (
    id BIGINT PRIMARY KEY DEFAULT nextval('queue_task_id_seq'),
    clazz TEXT NOT NULL,
    state VARCHAR(16) NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(1024) NOT NULL, --
    owned_by    VARCHAR(1024) NOT NULL, --
    result      VARCHAR(1024) NOT NULL, --
    task_lock_id    BIGINT REFERENCES task_lock(id)
);