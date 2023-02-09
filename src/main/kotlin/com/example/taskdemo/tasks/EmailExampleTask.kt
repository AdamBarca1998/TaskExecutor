package com.example.taskdemo.tasks

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import java.time.Duration

@TaskAnnotation
class EmailExampleTask : Task {

    private val taskConfig = TaskConfig.Builder().build()

    override fun run(taskContext: TaskContext) {
        println("EmailExampleTask running...")
        Thread.sleep(Duration.ofSeconds(1))
    }

    override fun getConfig(): TaskConfig {
        return taskConfig
    }
}