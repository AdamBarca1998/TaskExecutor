package com.example.taskdemo.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import java.time.Instant

@Entity
@Table(name = "task_lock")
class TaskLockEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_lock_id_gen")
    @SequenceGenerator(name = "task_lock_id_gen", sequenceName = "task_lock_id_seq", allocationSize = 1)
    @Column(nullable = false)
    val id: Long,

    @Size(max = 64)
    @Column(nullable = false, length = 64)
    val name: String,

    @Column(nullable = false)
    val lockUntil: Instant,

    @Column(nullable = false)
    val lockedAt: Instant,

    @Size(max = 256)
    @Column(nullable = false, length = 256)
    val lockedBy: String,
)