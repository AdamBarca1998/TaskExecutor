package com.example.taskdemo.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import java.time.Instant

@Entity
@Table(name = "task_context")
open class TaskContextEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @NotNull
    @Column(name = "start_date_time", nullable = false)
    open val startDateTime: Instant = Instant.MAX

    @NotNull
    @Column(name = "last_execution", nullable = false)
    open val lastExecution: Instant = Instant.MAX

    @NotNull
    @Column(name = "last_completion", nullable = false)
    open val lastCompletion: Instant = Instant.MAX
}