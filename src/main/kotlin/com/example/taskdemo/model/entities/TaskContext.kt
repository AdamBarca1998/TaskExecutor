package com.example.taskdemo.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull
import java.time.ZonedDateTime

@Embeddable
open class TaskContext(

    @Column(name = "last_execution")
    open var lastExecution: ZonedDateTime?,

    @Column(name = "last_completion")
    open var lastCompletion: ZonedDateTime?,

    @NotNull
    @Column(name = "next_execution", nullable = false)
    open var nextExecution: ZonedDateTime
) {
    constructor() : this(null, null, ZonedDateTime.now()) {

    }

}