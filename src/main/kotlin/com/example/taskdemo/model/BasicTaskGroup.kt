package com.example.taskdemo.model

import com.cronutils.builder.CronBuilder
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.field.expression.FieldExpressionFactory
import com.example.taskdemo.interfaces.TaskGroup
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BasicTaskGroup : TaskGroup {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val cronEvery15s = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
        .withSecond(FieldExpressionFactory.every(15))
        .instance()

    init {
        addAllAndRun(listOf(
            Task("Task 10 seconds", ZonedDateTime.now().plusSeconds(10)),
            Task("Task 20 seconds", ZonedDateTime.now().plusSeconds(20), cron = cronEvery15s),
            Task("Task 30 seconds", ZonedDateTime.now().plusSeconds(30)),
        ))
    }

    override fun addAllAndRun(tasks: List<Task>) {
        tasks.forEach { asyncRun(it) }
    }

    private fun asyncRun(task: Task) {
        scope.launch {
            delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), task.startTime)) // start

            do {
                task.run()
                delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), task.getNextTime() ?: ZonedDateTime.now())) // period
            } while (task.cron != null)
        }
    }
}