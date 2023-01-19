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
        val taskConfig5s = TaskConfig.Builder()
            .withTaskSchedules(listOf(getTaskScheduleEvery(5)))
            .build()
        val taskConfig7s = TaskConfig.Builder()
            .withTaskSchedules(listOf(getTaskScheduleEvery(7)))
            .build()
        val taskConfig10sHeavy = TaskConfig.Builder()
            .withTaskSchedules(listOf(getTaskScheduleEvery(10)))
            .withHeavy(true)
            .build()
        val taskConfig8sHeavy = TaskConfig.Builder()
            .withTaskSchedules(listOf(getTaskScheduleEvery(8)))
            .withHeavy(true)
            .build()

        taskService.runSchedule(TaskImpl("Task 5s cron"), getTaskContextNow(), taskConfig5s)
        taskService.runSchedule(TaskImpl("Task 7s cron"), getTaskContextNow(), taskConfig7s)
        taskService.runSchedule(TaskImpl("Task 8s cron heavy"), getTaskContextNow(), taskConfig8sHeavy)
        taskService.runSchedule(TaskImpl("Task 10s cron heavy"), getTaskContextNow(), taskConfig10sHeavy)

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
    fun testTaskScheduleHeavyAndPriority() {
        val taskConfig5sHeavy = TaskConfig.Builder()
            .withTaskSchedules(listOf(getTaskScheduleEvery(5)))
            .withHeavy(true)
            .build()
        val taskConfig5sPriority = TaskConfig.Builder()
            .withTaskSchedules(listOf(getTaskScheduleEvery(5)))
            .withPriority(100)
            .build()
        val taskConfig5s = TaskConfig.Builder()
            .withTaskSchedules(listOf(getTaskScheduleEvery(5)))
            .build()
        val taskConfig7sHeavy = TaskConfig.Builder()
        .withTaskSchedules(listOf(getTaskScheduleEvery(7)))
            .withHeavy(true)
            .build()
        val now = ZonedDateTime.now()
        val context1 = TaskContext(TaskScheduleContext(now, ZonedDateTime.now(), ZonedDateTime.now()), true)
        val context2 = TaskContext(TaskScheduleContext(now, ZonedDateTime.now(), ZonedDateTime.now()), true)

        taskService.runSchedule(TaskImpl("Task 5s cron heavy"), getTaskContextNow(), taskConfig5sHeavy)
        taskService.runSchedule(TaskImpl("Task 5s cron"), context1, taskConfig5s)
        taskService.runSchedule(TaskImpl("Task 7s cron heavy"), getTaskContextNow(), taskConfig7sHeavy)
        taskService.runSchedule(TaskImpl("Task 5s cron priority"), context2, taskConfig5sPriority)

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
        return TaskContext(TaskScheduleContext(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now()), true)
    }
}