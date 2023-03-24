package com.example.taskdemo.tasks.queues

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskContext
import java.time.Duration
import kotlinx.serialization.Serializable

@Serializable
class EmailTaskExample(
    private val receiver: String,
    override val id: Long = -1
) : Task {

    override fun run(taskContext: TaskContext) {
        println("Sending email to $receiver")
        Thread.sleep(Duration.ofSeconds(15))
    }
}