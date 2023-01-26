package com.example.taskdemo.model

import java.time.ZonedDateTime

interface Task {

    fun run(taskContext: TaskContext)

    fun nextExecution(): ZonedDateTime? {
        throw UnsupportedOperationException("Method is not implemented for task.")
    }
}
