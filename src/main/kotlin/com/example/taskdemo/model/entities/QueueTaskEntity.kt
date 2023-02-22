package com.example.taskdemo.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import java.time.Instant

@Entity
@Table(name = "queue_task")
open class QueueTaskEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "queue_task_id_gen")
    @SequenceGenerator(name = "queue_task_id_gen", sequenceName = "queue_task_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    open val id: Long,

    @NotNull
    @Column(name = "clazz", nullable = false, length = Integer.MAX_VALUE)
    open val clazz: String
)