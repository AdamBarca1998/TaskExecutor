package com.example.taskdemo.service

import com.cronutils.builder.CronBuilder
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.field.expression.FieldExpressionFactory.every
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.TaskImpl
import com.example.taskdemo.model.TaskScheduleContext
import com.example.taskdemo.taskgroup.QueueTaskGroup
import com.example.taskdemo.taskgroup.ScheduledTaskGroup
import com.example.taskdemo.taskschedule.TaskSchedule
import java.time.ZonedDateTime
import org.springframework.stereotype.Service


@Service
class TaskService {

    private val scheduledTaskGroup = ScheduledTaskGroup()
    private val queueTaskGroup = QueueTaskGroup()

    init {
        demoScheduled()
        demoQueue()
    }

    fun runSchedule(task: Task, context: TaskContext, config: TaskConfig) {
        scheduledTaskGroup.addTask(task, context, config)
    }

    fun runQueue(task: Task) {
        queueTaskGroup.addTask(task)
    }

    fun runDaemon(task: Task) {

    }

    fun removeTask(task: Task) {
        scheduledTaskGroup.removeTask(task)
        queueTaskGroup.removeTask(task)
    }

    private fun demoQueue() {
        queueTaskGroup.stop()

        val task4 = TaskImpl("Task linked 4")

        runQueue(TaskImpl("Task linked 1"))
        runQueue(TaskImpl("Task linked 2"))
        runQueue(TaskImpl("Task linked 3"))
        runQueue(task4)
        runQueue(TaskImpl("Task linked 5"))
        runQueue(TaskImpl("Task linked 6"))

        Thread.sleep(5_000)
        removeTask(task4)

        Thread.sleep(10_000)
        queueTaskGroup.start()
    }

    private fun demoScheduled() {
        val taskScheduleEvery5S = TaskSchedule.fromCron(CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
            .withSecond(every(5))
            .instance())
        val taskScheduleEvery7S = TaskSchedule.fromCron(CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
            .withSecond(every(5))
            .instance())


        val taskConfigEvery5s = TaskConfig.Builder()
            .taskSchedules(listOf(taskScheduleEvery5S))
            .startDateTime(ZonedDateTime.now().plusSeconds(10))
            .build()
        val taskConfigEvery7s = TaskConfig.Builder()
            .taskSchedules(listOf(taskScheduleEvery7S))
            .build()
        val taskConfigEvery5s7s = TaskConfig.Builder()
            .taskSchedules(listOf(taskScheduleEvery5S, taskScheduleEvery7S))
            .build()
        val taskContextNow = TaskContext(TaskScheduleContext(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now()))
        val taskContextAfter20s = TaskContext(TaskScheduleContext(ZonedDateTime.now().plusSeconds(20), ZonedDateTime.now(), ZonedDateTime.now()))

        val task7s = TaskImpl("Task every 7 seconds")

        runSchedule(TaskImpl("Task every 5 seconds + start after 20s"), taskContextAfter20s, taskConfigEvery5s)
        runSchedule(TaskImpl("Task every 5 seconds + every 7 seconds"), taskContextNow, taskConfigEvery5s7s)
        runSchedule(task7s, taskContextNow, taskConfigEvery7s)

        Thread.sleep(20_000)

        removeTask(task7s)
    }
}
