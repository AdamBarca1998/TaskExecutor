package com.example.taskdemo.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Embedded
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
import jakarta.validation.constraints.Size
import org.springframework.transaction.annotation.Transactional

@Entity
@Table(name = "daemon_task")
@Transactional
open class DaemonTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daemon_task_id_gen")
    @SequenceGenerator(name = "daemon_task_id_gen", sequenceName = "daemon_task_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Size(max = 1024)
    @NotNull
    @Column(name = "clazz_path", nullable = false, length = 1024)
    open var clazzPath: String = ""

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_lock_id", nullable = false)
    open var taskLockEntity: TaskLockEntity = TaskLockEntity()

    @Embedded
    open var taskContext: TaskContext = TaskContext()
}