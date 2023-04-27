package com.example.taskdemo.tasks.schedules

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskContext
import java.time.Duration
import java.time.LocalDateTime

//@ScheduleTask(
//    schedules = [
//        Schedule(second = "*/15")
//    ],
//    startDateTime = "2023-02-09T16:22:00Z",
//    type = ScheduleTaskType.HEAVY
//)
class HeavyScheduleTaskExample : Task {

    override var id = -1L

    override fun run(taskContext: TaskContext) {
        println("${LocalDateTime.now()}  HeavyScheduleTaskExample running...")
        Thread.sleep(Duration.ofSeconds(5))
    }
}