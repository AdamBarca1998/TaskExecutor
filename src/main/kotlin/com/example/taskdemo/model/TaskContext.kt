package com.example.taskdemo.model

import java.time.Instant

class TaskContext(
    val startDateTime: Instant,
    val lastExecution: Instant,
    var lastCompletion: Instant,
    var nextExecution: Instant?
)
