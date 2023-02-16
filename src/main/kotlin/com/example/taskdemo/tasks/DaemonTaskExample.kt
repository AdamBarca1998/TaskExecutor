package com.example.taskdemo.tasks

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskContext
import java.time.Duration

class DaemonTaskExample : Task {

    override fun run(taskContext: TaskContext) {
        println("EmailExampleTask running...")
        Thread.sleep(Duration.ofSeconds(1))
    }
}