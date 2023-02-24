package com.example.taskdemo.model.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import java.time.Instant

@Entity
@Table(name = "queue_task")
open class QueueTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "queue_task_id_gen")
    @SequenceGenerator(name = "queue_task_id_gen", sequenceName = "queue_task_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @NotNull
    @Column(name = "clazz", nullable = false, length = Integer.MAX_VALUE)
    open var clazz: String? = null

    @NotNull
    @Column(name = "created_at", nullable = false)
    open var createdAt: Instant? = Instant.now()

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "task_lock_id")
    open var taskLockEntity: TaskLockEntity? = null
}