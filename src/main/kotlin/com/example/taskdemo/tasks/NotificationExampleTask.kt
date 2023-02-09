package com.example.taskdemo.tasks

import com.cronutils.builder.CronBuilder
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.field.expression.FieldExpressionFactory
import com.example.taskdemo.TaskType
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.taskschedule.TaskSchedule
import java.time.Duration

@TaskAnnotation
class NotificationExampleTask : Task {

    private val taskConfig = TaskConfig.Builder()
        .withTaskSchedules(listOf(
            TaskSchedule.fromCron(CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
                .withSecond(FieldExpressionFactory.every(5))
                .instance())
            )
        )
        .withType(TaskType.SCHEDULED)
        .build()

    override fun run(taskContext: TaskContext) {
        println("NotificationExampleTask running...")
        Thread.sleep(Duration.ofSeconds(1))
    }

    override fun getConfig(): TaskConfig {
        return taskConfig
    }
}