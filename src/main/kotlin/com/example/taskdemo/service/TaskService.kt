package com.example.taskdemo.service

import com.cronutils.builder.CronBuilder
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.field.expression.FieldExpressionFactory
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.TaskImpl
import com.example.taskdemo.model.TaskSchedule
import com.example.taskdemo.model.TaskScheduleContext
import com.example.taskdemo.taskgroup.QueueTaskGroup
import com.example.taskdemo.taskgroup.ScheduledTaskGroup
import java.time.ZonedDateTime
import org.springframework.stereotype.Service


@Service
class TaskService {

    private val scheduledTaskGroup = ScheduledTaskGroup()
    private val queueTaskGroup = QueueTaskGroup()

    init {
        demoScheduled()
//        demoQueue()
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

    }

    private fun demoQueue() {
        queueTaskGroup.stop()

        runQueue(TaskImpl("Task linked 1"))
        runQueue(TaskImpl("Task linked 2"))
        runQueue(TaskImpl("Task linked 3"))
        runQueue(TaskImpl("Task linked 4"))
        runQueue(TaskImpl("Task linked 5"))
        runQueue(TaskImpl("Task linked 6"))

        Thread.sleep(10_000)
        queueTaskGroup.start()
    }

    private fun demoScheduled() {
        scheduledTaskGroup.stop()

        val taskScheduleEvery5s = TaskSchedule(CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
            .withSecond(FieldExpressionFactory.every(5))
            .instance()
        )
        val taskScheduleEvery7s = TaskSchedule(CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
            .withSecond(FieldExpressionFactory.every(7))
            .instance()
        )
        val taskConfigEvery5s = TaskConfig.Builder()
            .taskSchedules(listOf(taskScheduleEvery5s))
            .startDateTime(ZonedDateTime.now().plusSeconds(10))
            .build()
        val taskConfigEvery7s = TaskConfig.Builder()
            .taskSchedules(listOf(taskScheduleEvery7s))
            .build()
        val taskContextNow = TaskContext(TaskScheduleContext(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now()))
        val taskContextAfter20s = TaskContext(TaskScheduleContext(ZonedDateTime.now().plusSeconds(20), ZonedDateTime.now(), ZonedDateTime.now()))

        runSchedule(TaskImpl("Task every 5 seconds + start after 20s"), taskContextAfter20s, taskConfigEvery5s)
        runSchedule(TaskImpl("Task every 7 seconds"), taskContextNow, taskConfigEvery7s)

        Thread.sleep(10_000)
        scheduledTaskGroup.start()
    }
}
