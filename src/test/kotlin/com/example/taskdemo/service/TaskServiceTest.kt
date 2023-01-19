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
        val taskConfig = TaskConfig.Builder()
            .withTaskSchedules(listOf(taskScheduleDelay5s))
            .build()

        taskService.runSchedule(TaskImpl("Task 5s delay"), getTaskContextNow(), taskConfig)

        Thread.sleep(hour1)
    }

    @Test
    fun testTaskRate() {
        val taskScheduleRate5s = TaskSchedule.fromFixedRate(Duration.ofSeconds(5))
        val taskConfig = TaskConfig.Builder()
            .withTaskSchedules(listOf(taskScheduleRate5s))
            .build()

        taskService.runSchedule(TaskImpl("Task 5s rate"), getTaskContextNow(), taskConfig)

        Thread.sleep(hour1)
    }

    @Test
    fun testTaskCron() {
        val taskConfig = TaskConfig.Builder()
            .withTaskSchedules(listOf(getTaskScheduleEvery(5)))
            .build()

        taskService.runSchedule(TaskImpl("Task 5s cron"), getTaskContextNow(), taskConfig)

        Thread.sleep(hour1)
    }

    @Test
    fun testTaskQueue() {
        taskService.stopQueue()

        Thread.sleep(Duration.ofSeconds(5))

        taskService.runQueue(TaskImpl("Task linked 1"))
        taskService.runQueue(TaskImpl("Task linked 2"))
        taskService.runQueue(TaskImpl("Task linked 3"))

        taskService.startQueue()

        Thread.sleep(hour1)
    }

    @Test
    fun testTaskScheduleHeavy() {
        val taskConfig5sHeavy = TaskConfig.Builder()
            .withTaskSchedules(listOf(getTaskScheduleEvery(5)))
            .withHeavy(true)
            .build()
        val taskConfig = TaskConfig.Builder()
            .withTaskSchedules(listOf(getTaskScheduleEvery(5)))
            .build()
        val taskConfig7sHeavy = TaskConfig.Builder()
        .withTaskSchedules(listOf(getTaskScheduleEvery(7)))
            .withHeavy(true)
            .build()

        taskService.runSchedule(TaskImpl("Task 5s cron heavy"), getTaskContextNow(), taskConfig5sHeavy)
        taskService.runSchedule(TaskImpl("Task 5s cron"), getTaskContextNow(), taskConfig)
        taskService.runSchedule(TaskImpl("Task 7s cron heavy"), getTaskContextNow(), taskConfig7sHeavy)

        Thread.sleep(hour1)
    }

    private fun getTaskScheduleEvery(time: Int): TaskSchedule {
        return TaskSchedule.fromCron(
            CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
                .withSecond(FieldExpressionFactory.every(time))
                .instance()
        )
    }

    private fun getTaskContextNow(): TaskContext {
        return TaskContext(TaskScheduleContext(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now()))
    }
}