package com.example.taskdemo.annotations

import com.example.taskdemo.enums.ScheduleTaskType

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ScheduleTask(
    val type: ScheduleTaskType = ScheduleTaskType.NORMAL,
    val priority: Int = Int.MIN_VALUE,
    val startDateTime: String = "NOW", // 2021-10-01T05:06:20Z
    val description: String = "",
    val maxWaitDuration: String = "PT24H", //PT10H30M30.100S

    val schedules: Array<Schedule>
)