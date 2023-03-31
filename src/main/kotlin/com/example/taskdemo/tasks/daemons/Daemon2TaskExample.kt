package com.example.taskdemo.tasks.daemons

import com.example.taskdemo.annotations.TaskDaemon
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime

@TaskDaemon
class Daemon2TaskExample : Task {

    override var id = -1L

    override fun run(taskContext: TaskContext) {
        println("\"${LocalDateTime.now()}   Daemon2TaskExample running...")
        Thread.sleep(Duration.ofSeconds(1))
        taskContext.nextExecution = ZonedDateTime.now().plusSeconds(15)
    }
}