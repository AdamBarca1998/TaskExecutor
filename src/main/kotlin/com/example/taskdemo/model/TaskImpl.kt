package com.example.taskdemo.model

import java.time.Duration

data class TaskImpl(
    val name: String,
) : Task {

    override fun run(taskContextAbstract: TaskContextAbstract?) {
        println("$name running...")
        Thread.sleep(Duration.ofSeconds(7))
    }
}
