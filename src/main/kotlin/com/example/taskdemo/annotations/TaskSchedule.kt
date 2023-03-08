package com.example.taskdemo.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TaskSchedule(
    val heavy: Boolean = false,
    val priority: Int = Int.MIN_VALUE,
    val startDateTime: String = "", // 2021-10-01T05:06:20Z
    val description: String = "",

    val schedules: Array<Schedule>
)