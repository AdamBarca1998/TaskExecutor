package com.example.taskdemo.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TaskSchedule(
    val heavy: Boolean = false,
    val priority: Int = Int.MIN_VALUE,
    val startDateTime: String = "NOW", // 2021-10-01T05:06:20Z
    val description: String = "",
    val maxWaitDuration: String = "PT0H", //PT10H30M30.100S

    val schedules: Array<Schedule>
)