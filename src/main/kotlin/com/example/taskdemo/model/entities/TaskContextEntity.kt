package com.example.taskdemo.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import java.time.ZonedDateTime

@Entity
@Table(name = "task_context")
open class TaskContextEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @NotNull
    @Column(name = "start_date_time", nullable = false)
    open var startDateTime: ZonedDateTime = ZonedDateTime.now()

    @NotNull
    @Column(name = "last_execution", nullable = false)
    open var lastExecution: ZonedDateTime = ZonedDateTime.now()

    @NotNull
    @Column(name = "last_completion", nullable = false)
    open var lastCompletion: ZonedDateTime = ZonedDateTime.now()
}