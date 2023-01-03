package com.example.taskdemo.model

import java.time.ZonedDateTime

data class TaskImpl(
    val name: String,
) : Task {

    override fun run(taskContextAbstract: TaskContextAbstract?) {
        println("$name                     \t                                   ${ZonedDateTime.now()}")
    }
}
