package com.example.taskdemo.model

import java.time.Duration
import java.time.ZonedDateTime

data class Daemon(val name: String) : Task {

    private val events = listOf(
        ZonedDateTime.now().plusSeconds(10),
        ZonedDateTime.now().plusSeconds(30),
        ZonedDateTime.now().plusSeconds(60)
    )

    override fun run(taskContext: TaskContext) {
        println("$name running...")
        Thread.sleep(Duration.ofSeconds(1))
    }

    override fun nextExecution(): ZonedDateTime? {
        return events.find { ZonedDateTime.now().isBefore(it) }
    }
}