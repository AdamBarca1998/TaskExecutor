package com.example.taskdemo.tasks.daemons

import com.example.taskdemo.annotations.TaskDaemon
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

@TaskDaemon
class Daemon2TaskExample : Task {

    override val id = -1L

    override fun run(taskContext: TaskContext) {
        println("\"${LocalDateTime.now()}   Daemon2TaskExample running...")
        Thread.sleep(Duration.ofSeconds(30))
        taskContext.nextExecution = Instant.now().plusSeconds(30)
    }
}