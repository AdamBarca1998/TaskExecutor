package com.example.taskdemo.tasks.queues

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskContext
import java.time.Duration
import kotlinx.serialization.Serializable

@Serializable
class QueueTaskErrorExample(
    private val receiver: String,
    override var id: Long = -1
) : Task {

    override fun run(taskContext: TaskContext) {
        println("Hello to $receiver")
        Thread.sleep(Duration.ofSeconds(5))
        throw NullPointerException("QueueTaskErrorExample")
    }
}