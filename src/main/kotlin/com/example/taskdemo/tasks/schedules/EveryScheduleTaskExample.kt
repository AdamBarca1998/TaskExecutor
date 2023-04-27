package com.example.taskdemo.tasks.schedules

import com.example.taskdemo.annotations.Schedule
import com.example.taskdemo.annotations.ScheduleTask
import com.example.taskdemo.enums.ScheduleTaskType
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskContext
import java.time.Duration
import java.time.LocalDateTime

@ScheduleTask(
    schedules = [
        Schedule(second = "*/15")
    ],
    startDateTime = "2023-02-09T16:22:00Z",
    type = ScheduleTaskType.EVERY
)
class EveryScheduleTaskExample : Task {

    override var id = -1L

    override fun run(taskContext: TaskContext) {
        println("${LocalDateTime.now()}  EveryScheduleTaskExample running...")
        Thread.sleep(Duration.ofSeconds(5))
    }
}