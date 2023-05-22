package com.example.taskdemo.tasks.daemons

import com.example.taskdemo.annotations.DaemonTask
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime

@DaemonTask
class Daemon2TaskExample : Task {

    override var id = -1L

    override fun run(taskContext: TaskContext) {
        println("\"${LocalDateTime.now()}   Daemon2TaskExample running...")
        Thread.sleep(Duration.ofMinutes(1))
        taskContext.nextExecution = ZonedDateTime.now().plusMinutes(1)
    }
}