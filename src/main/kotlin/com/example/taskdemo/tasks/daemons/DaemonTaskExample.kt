package com.example.taskdemo.tasks.daemons

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

//@TaskDaemon
class DaemonTaskExample : Task {

    override var id = -1L

    override fun run(taskContext: TaskContext) {
        println("\"${LocalDateTime.now()}   DaemonTaskExample running...")
        Thread.sleep(Duration.ofSeconds(1))
        taskContext.nextExecution = Instant.now().plusSeconds(60)
    }
}