package com.example.taskdemo.model

import com.cronutils.model.Cron
import com.cronutils.model.time.ExecutionTime
import com.example.taskdemo.extensions.toNullable
import java.time.ZonedDateTime

data class TaskSchedule(
    val cronList: List<Cron>? = null
) {

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
}
