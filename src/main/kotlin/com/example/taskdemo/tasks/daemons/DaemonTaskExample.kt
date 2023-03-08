package com.example.taskdemo.tasks.daemons

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskContext
import java.time.Duration


class DaemonTaskExample : Task {

    override val id = -1L

    override fun run(taskContext: TaskContext) {
        println("EmailExampleTask running...")
        Thread.sleep(Duration.ofSeconds(1))
    }
}