package com.example.taskdemo.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull
import java.time.ZonedDateTime

@Embeddable
open class TaskContext(

    @NotNull
    @Column(name = "start_date_time", nullable = false)
    open var startDateTime: ZonedDateTime,

    @NotNull
    @Column(name = "last_execution", nullable = false)
    open var lastExecution: ZonedDateTime,

    @NotNull
    @Column(name = "last_completion", nullable = false)
    open var lastCompletion: ZonedDateTime,

    @Column(name = "next_execution")
    open var nextExecution: ZonedDateTime?
) {
    constructor() : this(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now()) {

    }

}