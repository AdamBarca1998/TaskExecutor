package com.example.taskdemo.model

import com.cronutils.model.Cron
import com.cronutils.model.time.ExecutionTime
import com.example.taskdemo.extensions.toNullable
import java.time.ZonedDateTime

data class TaskExecutor(
    val name: String,
    val startTime: ZonedDateTime,
    val priority: Int? = null,
    val cronList: List<Cron>? = null
) : Task, Comparable<TaskExecutor> {

    fun getNextTime(): ZonedDateTime? {
        if (cronList == null) {
            return null
        }

        val sortExecution = cronList.stream()
            .map { ExecutionTime.forCron(it).nextExecution(ZonedDateTime.now()).toNullable() }
            .sorted()
            .toList()

        return sortExecution.first()
    }

    override fun run(taskContext: TaskContext) {
        println("$name \t $priority \t ${ZonedDateTime.now()} \t ${getNextTime()}")
    }

    override fun compareTo(other: TaskExecutor): Int {
        return (this.priority ?: Int.MIN_VALUE).compareTo(other.priority ?: Int.MIN_VALUE)
    }
}
