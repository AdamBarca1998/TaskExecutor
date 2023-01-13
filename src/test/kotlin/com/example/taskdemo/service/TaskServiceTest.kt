package com.example.taskdemo.service

import com.cronutils.builder.CronBuilder
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.field.expression.FieldExpressionFactory
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.TaskImpl
import com.example.taskdemo.model.TaskScheduleContext
import com.example.taskdemo.taskschedule.TaskSchedule
import java.time.Duration
import java.time.ZonedDateTime
import org.junit.jupiter.api.Test

internal class TaskServiceTest {

    private val taskService = TaskService()
    private val hour1 = Duration.ofHours(1)

    @Test
    fun testTaskDelay() {
        val taskScheduleDelay5s = TaskSchedule.fromFixedDelay(Duration.ofSeconds(5))
        val taskContextNow = TaskContext(TaskScheduleContext(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now()))
        val taskConfig = TaskConfig.Builder()
            .taskSchedules(listOf(taskScheduleDelay5s))
            .build()

        taskService.runSchedule(TaskImpl("Task 5s delay"), taskContextNow, taskConfig)

        Thread.sleep(hour1)
    }

    @Test
    fun testTaskRate() {
        val taskScheduleRate5s = TaskSchedule.fromFixedRate(Duration.ofSeconds(5))
        val taskContextNow = TaskContext(TaskScheduleContext(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now()))
        val taskConfig = TaskConfig.Builder()
            .taskSchedules(listOf(taskScheduleRate5s))
            .build()

        taskService.runSchedule(TaskImpl("Task 5s rate"), taskContextNow, taskConfig)

        Thread.sleep(hour1)
    }

    @Test
    fun testTaskCron() {
        val taskScheduleEvery5S = TaskSchedule.fromCron(
            CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
            .withSecond(FieldExpressionFactory.every(5))
            .instance())
        val taskContextNow = TaskContext(TaskScheduleContext(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now()))
        val taskConfig = TaskConfig.Builder()
            .taskSchedules(listOf(taskScheduleEvery5S))
            .build()

        taskService.runSchedule(TaskImpl("Task 5s cron"), taskContextNow, taskConfig)

        Thread.sleep(hour1)
    }
}