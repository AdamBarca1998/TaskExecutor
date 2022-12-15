package com.example.taskdemo.service

import com.cronutils.builder.CronBuilder
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.field.expression.FieldExpressionFactory.every
import com.example.taskdemo.executor.TaskExecutor
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskGroup
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.time.ZonedDateTime


@Service
class TaskService {

    private val cronEvery15s = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
        .withSecond(every(15))
        .instance()

    private val tasks = listOf(
        Task("Task 10 seconds", ZonedDateTime.now().plusSeconds(10), TaskGroup("TaskGroup1")),
        Task("Task 20 seconds", ZonedDateTime.now().plusSeconds(20), TaskGroup("TaskGroup2"), cron = cronEvery15s),
        Task("Task 30 seconds", ZonedDateTime.now().plusSeconds(30), TaskGroup("TaskGroup1")),

        // priority
        Task("Task priority 5", ZonedDateTime.now(), TaskGroup("TaskGroup1"), priority = 5),
        Task("Task priority 6", ZonedDateTime.now(), TaskGroup("TaskGroup1"), priority = 6),
        Task("Task priority 3", ZonedDateTime.now(), TaskGroup("TaskGroup1"), priority = 3),
        Task("Task priority -5", ZonedDateTime.now(), TaskGroup("TaskGroup1"), priority = -5),
        Task("Task priority 2", ZonedDateTime.now(), TaskGroup("TaskGroup1"), priority = 2),
        Task("Task priority 1", ZonedDateTime.now(), TaskGroup("TaskGroup1"), priority = 1),
        Task("Task priority null", ZonedDateTime.now(), TaskGroup("TaskGroup1"))
    )

    private val taskExecutor = TaskExecutor()

    @PostConstruct
    private fun initTasks() {
        taskExecutor.runTasks(tasks)
    }
}
