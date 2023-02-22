DROP TABLE IF EXISTS schedule_task;
DROP TABLE IF EXISTS task_lock;
DROP SEQUENCE IF EXISTS schedule_task_id_seq;
DROP SEQUENCE IF EXISTS task_lock_id_seq;

CREATE SEQUENCE schedule_task_id_seq
INCREMENT 1
START 1;

CREATE SEQUENCE task_lock_id_seq
INCREMENT 1
START 1;


CREATE TABLE task_lock (
    id  BIGINT PRIMARY KEY DEFAULT nextval('task_lock_id_seq'),
    name    VARCHAR(64) NOT NULL UNIQUE,
    lock_until  TIMESTAMP NOT NULL,
    locked_at   TIMESTAMP NOT NULL,
    locked_by   VARCHAR(256) NOT NULL
);

CREATE TABLE schedule_task (
    id  BIGINT  PRIMARY KEY DEFAULT nextval('schedule_task_id_seq'),
    clazz   VARCHAR(1024) NOT NULL UNIQUE,
    enable  BOOLEAN NOT NULL DEFAULT TRUE,
    task_lock_id    BIGINT REFERENCES task_lock(id)
);