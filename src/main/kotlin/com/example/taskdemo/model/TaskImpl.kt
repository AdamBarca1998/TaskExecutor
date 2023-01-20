package com.example.taskdemo.model

import java.time.Duration

data class TaskImpl(
    val name: String,
) : Task {

    override fun run(taskContext: TaskContext) {
        println("$name running...")
        Thread.sleep(Duration.ofSeconds(1))
    }
}
