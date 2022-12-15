package com.example.taskdemo.model

import com.cronutils.model.Cron
import com.cronutils.model.time.ExecutionTime
import com.example.taskdemo.extensions.toNullable
import java.time.ZonedDateTime

data class Task(
    val name: String,
    val startTime: ZonedDateTime,
    val taskGroup: TaskGroup,
    val priority: Int? = null,
    val cron: Cron? = null
) : Comparable<Task> {
    fun doIt() {
        println("$name \t $priority \t ${ZonedDateTime.now()} \t ${getNextTime()}")
    }

    fun getNextTime(): ZonedDateTime? {
        if (cron == null) {
            return null
        }

        val executionTime = ExecutionTime.forCron(cron)

        return executionTime.nextExecution(ZonedDateTime.now()).toNullable()
    }

    override fun compareTo(other: Task): Int {
        return (this.priority ?: Int.MIN_VALUE).compareTo(other.priority ?: Int.MIN_VALUE)
    }
}
