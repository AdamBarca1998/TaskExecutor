package com.example.taskdemo.tasks.schedules

import com.example.taskdemo.annotations.Schedule
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.annotations.TaskSchedule
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@TaskSchedule(
    schedules = [
        Schedule(second = "*/10"),
        Schedule(fixedRate = 7, timeUnit = TimeUnit.SECONDS)
    ],
    startDateTime = "2023-02-09T16:22:00Z"
)
class ScheduleTaskExample : Task {

    override val id = -1L

    override fun run(taskContext: TaskContext) {
        println("${LocalDateTime.now()}  ScheduleTaskExample running...")
        Thread.sleep(Duration.ofSeconds(1))
    }
}