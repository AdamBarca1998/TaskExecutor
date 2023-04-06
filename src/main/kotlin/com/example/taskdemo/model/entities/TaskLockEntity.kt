package com.example.taskdemo.model.entities

import com.example.taskdemo.taskgroup.CLUSTER_NAME
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant

@Entity
@Table(name = "task_lock")
open class TaskLockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_lock_id_gen")
    @SequenceGenerator(name = "task_lock_id_gen", sequenceName = "task_lock_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Size(max = 1024)
    @NotNull
    @Column(name = "name", nullable = false, length = 1024)
    open var name: String = ""

    @NotNull
    @Column(name = "lock_until", nullable = false)
    open var lockUntil: Instant = Instant.EPOCH

    @NotNull
    @Column(name = "locked_at", nullable = false)
    open var lockedAt: Instant = Instant.EPOCH

    @Size(max = 256)
    @NotNull
    @Column(name = "locked_by", nullable = false, length = 256)
    open var lockedBy: String = "nobody"

    @Size(max = 256)
    @NotNull
    @Column(name = "cluster_name", nullable = false, length = 256)
    open var clusterName: String = CLUSTER_NAME
}