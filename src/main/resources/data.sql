INSERT INTO task_lock(name, lock_until, locked_at, locked_by)
VALUES('scheduleGroup', to_timestamp(0), to_timestamp(0), 'nobody');