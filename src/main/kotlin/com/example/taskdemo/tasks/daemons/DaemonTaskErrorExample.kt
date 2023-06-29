package com.example.taskdemo.tasks.daemons

import com.example.taskdemo.annotations.DaemonTask
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime

//@DaemonTask
class DaemonTaskErrorExample : Task {

    override var id = -1L

    override fun run(taskContext: TaskContext) {
        println("\"${LocalDateTime.now()}   DaemonTaskErrorExample running...")
        Thread.sleep(Duration.ofMinutes(1))
        taskContext.nextExecution = ZonedDateTime.now().plusMinutes(1)
        throw NullPointerException("DaemonTaskErrorExample")
    }
}