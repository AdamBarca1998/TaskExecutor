package com.example.taskdemo.taskgroup

import com.cronutils.builder.CronBuilder
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.field.expression.FieldExpressionFactory.every
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.TaskExecutor
import kotlinx.coroutines.*
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class BasicTaskGroup : TaskGroupAbstract() {

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        val cronEvery7s = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
            .withSecond(every(7))
            .instance()
        val cronEvery5s = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
            .withSecond(every(5))
            .instance()

        addAndRun(TaskExecutor("Task 10 seconds", ZonedDateTime.now().plusSeconds(10)))
        addAndRun(TaskExecutor("Task 20 seconds Cron: 5s, 7s", ZonedDateTime.now().plusSeconds(20), cronList = listOf(cronEvery5s, cronEvery7s)))
        addAndRun(TaskExecutor("Task 30 seconds", ZonedDateTime.now().plusSeconds(30)))
    }

    fun scheduleRun() {

    }

    override fun run() {
        scope.launch {
            val task = withContext(Dispatchers.IO) {
                plannedTasks.take()
            }

            delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), task.startTime)) // start

            do {
                task.run(TaskContext())
                delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), task.getNextTime() ?: ZonedDateTime.now())) // period
            } while (task.cronList != null)
        }
    }
}
