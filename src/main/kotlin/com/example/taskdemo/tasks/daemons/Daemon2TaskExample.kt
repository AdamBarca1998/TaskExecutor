package com.example.taskdemo.tasks.daemons

import com.example.taskdemo.annotations.TaskDaemon
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskContext
import java.time.Duration

@TaskDaemon
class Daemon2TaskExample : Task {

    override val id = -1L

    override fun run(taskContext: TaskContext) {
        println("Daemon2TaskExample running...")
        Thread.sleep(Duration.ofSeconds(30))
    }
}