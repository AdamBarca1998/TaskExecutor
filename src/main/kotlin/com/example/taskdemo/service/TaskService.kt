package com.example.taskdemo.service

import com.cronutils.builder.CronBuilder
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.field.expression.FieldExpressionFactory
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskExecutor
import com.example.taskdemo.taskgroup.BasicTaskGroup
import com.example.taskdemo.taskgroup.FlowTaskGroup
import com.example.taskdemo.taskgroup.PriorityTaskGroup
import java.time.ZonedDateTime
import org.springframework.stereotype.Service


@Service
class TaskService {

    private val basicTaskGroup = BasicTaskGroup()
    private val priorityTaskGroup = PriorityTaskGroup()
    private val flowTaskGroup = FlowTaskGroup()

    init {
        // basic
        val cronEvery7s = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
            .withSecond(FieldExpressionFactory.every(7))
            .instance()
        val cronEvery5s = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
            .withSecond(FieldExpressionFactory.every(5))
            .instance()

        TaskExecutor("Task 10 seconds", ZonedDateTime.now().plusSeconds(10))
        TaskExecutor("Task 20 seconds Cron: 5s, 7s", ZonedDateTime.now().plusSeconds(20), cronList = listOf(cronEvery5s, cronEvery7s))
        TaskExecutor("Task 30 seconds", ZonedDateTime.now().plusSeconds(30))

        //
    }

    fun run(task: Task) {
        basicTaskGroup.addTask(task)
    }

    fun runSchedule(task: Task, config: TaskConfig) {
        basicTaskGroup.scheduleRun()
    }

    fun runDaemon(task: Task) {

    }
}
