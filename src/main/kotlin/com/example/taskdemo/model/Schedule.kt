package com.example.taskdemo.model

import java.util.concurrent.TimeUnit

const val DEFAULT_FIXED_RATE_OR_DELAY = -1L

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Schedule(
    // spring cron
    val second: String = "*",
    val minute: String = "*",
    val hour: String = "*",
    val dayOfMonth: String = "*",
    val month: String = "*",
    val dayOfWeek: String = "*",

    // fixed
    val fixedRate: Long = DEFAULT_FIXED_RATE_OR_DELAY,
    val fixedDelay: Long = DEFAULT_FIXED_RATE_OR_DELAY,
    val timeUnit: TimeUnit = TimeUnit.MILLISECONDS
)
