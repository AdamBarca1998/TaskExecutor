package com.example.taskdemo.tasks.daemons

import com.example.taskdemo.annotations.TaskDaemon
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime

@TaskDaemon
class DaemonTask1mExample : Task {

    override var id = -1L

    override fun run(taskContext: TaskContext) {
        println("\"${LocalDateTime.now()}   DaemonTask1mExample running...")
        Thread.sleep(Duration.ofSeconds(5))
        taskContext.nextExecution = taskContext.lastExecution?.plusMinutes(1) ?: ZonedDateTime.now()
    }
}