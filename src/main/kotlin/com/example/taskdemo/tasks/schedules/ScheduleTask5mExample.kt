package com.example.taskdemo.tasks.schedules

import com.example.taskdemo.annotations.Schedule
import com.example.taskdemo.annotations.TaskSchedule
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.LocalDateTime

@TaskSchedule(
    schedules = [
        Schedule(minute = "*/5")
    ],
    startDateTime = "2023-02-09T16:22:00Z"
)
class ScheduleTask5mExample : Task {

    override var id = -1L

    override fun run(taskContext: TaskContext) {
        println("${LocalDateTime.now()}  ScheduleTask5mExample running...")
        Thread.sleep(Duration.ofSeconds(5))
    }
}