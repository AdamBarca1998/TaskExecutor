package com.example.taskdemo.model.entities

import com.example.taskdemo.enums.QueueTaskState
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import java.time.ZonedDateTime

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
    open var clazz: String = ""

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 16)
    open var state: QueueTaskState = QueueTaskState.CREATED

    @NotNull
    @Column(name = "created_at", nullable = false)
    open var createdAt: ZonedDateTime = ZonedDateTime.now()

    @NotNull
    @Column(name = "created_by", nullable = false, length = 1024)
    open var createdBy: String = "nobobdy"

    @NotNull
    @Column(name = "owned_by", nullable = false, length = 1024)
    open var ownedBy: String = "all"

    @NotNull
    @Column(name = "result", nullable = false, length = 1024)
    open var result: String = ""

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "task_lock_id")
    open var taskLockEntity: TaskLockEntity = TaskLockEntity()
}