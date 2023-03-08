package com.example.taskdemo.tasks.daemons

import com.example.taskdemo.annotations.TaskDaemon
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskContext
import java.time.Duration

@TaskDaemon
class DaemonTaskExample : Task {

    override val id = -1L

    override fun run(taskContext: TaskContext) {
        println("DaemonTaskExample running...")
        Thread.sleep(Duration.ofSeconds(1))
    }
}