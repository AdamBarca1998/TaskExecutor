package com.example.taskdemo.service

import com.cronutils.builder.CronBuilder
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.field.expression.FieldExpressionFactory
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskImpl
import com.example.taskdemo.taskschedule.TaskSchedule
import java.time.Duration
import org.junit.jupiter.api.Test

internal class TaskServiceTest {

    private val taskService = TaskService()
    private val hour1 = Duration.ofHours(1)

    @Test
    fun testTaskDelay() {
        val taskConfig5Delay = TaskConfig.Builder()
            .withTaskSchedules(listOf(TaskSchedule.fromFixedDelay(Duration.ofSeconds(5))))
            .build()
        val taskConfig7Delay = TaskConfig.Builder()
            .withTaskSchedules(listOf(TaskSchedule.fromFixedDelay(Duration.ofSeconds(7))))
            .build()
        val taskConfig10Delay = TaskConfig.Builder()
            .withTaskSchedules(listOf(TaskSchedule.fromFixedDelay(Duration.ofSeconds(10))))
            .build()

        taskService.runSchedule(TaskImpl("Task 5s delay"), taskConfig5Delay)
        taskService.runSchedule(TaskImpl("Task 7s delay"), taskConfig7Delay)
        taskService.runSchedule(TaskImpl("Task 10s delay"), taskConfig10Delay)

        Thread.sleep(hour1)
    }

    @Test
    fun testTaskRate() {
        val taskConfig5Rate = TaskConfig.Builder()
            .withTaskSchedules(listOf(TaskSchedule.fromFixedRate(Duration.ofSeconds(5))))
            .build()
        val taskConfig7Rate = TaskConfig.Builder()
            .withTaskSchedules(listOf(TaskSchedule.fromFixedRate(Duration.ofSeconds(7))))
            .build()
        val taskConfig10Rate = TaskConfig.Builder()
            .withTaskSchedules(listOf(TaskSchedule.fromFixedRate(Duration.ofSeconds(10))))
            .build()

        taskService.runSchedule(TaskImpl("Task 5s rate"), taskConfig5Rate)
        taskService.runSchedule(TaskImpl("Task 7s rate"), taskConfig7Rate)
        taskService.runSchedule(TaskImpl("Task 10s rate"), taskConfig10Rate)

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

        taskService.runSchedule(TaskImpl("Task 5s cron"), taskConfig5s)
        taskService.runSchedule(TaskImpl("Task 7s cron"), taskConfig7s)
        taskService.runSchedule(TaskImpl("Task 8s cron heavy"), taskConfig8sHeavy)
        taskService.runSchedule(TaskImpl("Task 10s cron heavy"), taskConfig10sHeavy)

        Thread.sleep(hour1)
    }

    @Test
    fun testTaskQueue() {
        taskService.stopQueue()

        taskService.runQueue(TaskImpl("Task linked 1"))
        taskService.runQueue(TaskImpl("Task linked 2"))
        taskService.runQueue(TaskImpl("Task linked 3"))

        Thread.sleep(Duration.ofSeconds(5))
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

        taskService.stopSchedule()

        taskService.runSchedule(TaskImpl("Task 5s cron heavy"), taskConfig5sHeavy)
        taskService.runSchedule(TaskImpl("Task 5s cron"), taskConfig5s)
        taskService.runSchedule(TaskImpl("Task 7s cron heavy"), taskConfig7sHeavy)
        taskService.runSchedule(TaskImpl("Task 5s cron priority"), taskConfig5sPriority)

        Thread.sleep(Duration.ofSeconds(30))
        taskService.startSchedule()

        Thread.sleep(hour1)
    }

    @Test
    fun testDaemonTask() {
        taskService.stopDaemon()

        taskService.runDaemon(TaskImpl("Daemon 1"))
        taskService.runDaemon(TaskImpl("Daemon 2"))
        taskService.runDaemon(TaskImpl("Daemon 3"))

        Thread.sleep(Duration.ofSeconds(5))
        taskService.startDaemon()

        Thread.sleep(hour1)
    }

    private fun getTaskScheduleEvery(time: Int): TaskSchedule {
        return TaskSchedule.fromCron(
            CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
                .withSecond(FieldExpressionFactory.every(time))
                .instance()
        )
    }
}