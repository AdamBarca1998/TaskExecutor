package com.example.taskdemo.model.dto

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskContext
import java.time.Duration

data class EmailTaskExampleDTO(
    val receiver: String = ""
) : Task {

    override fun run(taskContext: TaskContext) {
        println("Sending email to $receiver")
        Thread.sleep(Duration.ofSeconds(1))
    }
}