package com.example.taskdemo.tasks

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Schedule(
    val heavy: Boolean = false,
    val priority: Int = Int.MIN_VALUE,
    val startDateTime: String = "", // 2021-10-01T05:06:20Z
    val description: String = "",

    // spring cron
    val second: String = "*",
    val minute: String = "*",
    val hour: String = "*",
    val dayOfMonth: String = "*",
    val month: String = "*",
    val dayOfWeek: String = "*",

    // fixed
    val fixedRate: Long = -1,
    val fixedDelay: Long = -1,
    val timeUnit: TimeUnit = TimeUnit.MILLISECONDS
)