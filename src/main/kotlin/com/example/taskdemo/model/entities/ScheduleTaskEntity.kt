package com.example.taskdemo.model.entities

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
import jakarta.validation.constraints.Size

@Entity
@Table(name = "schedule_task")
class ScheduleTaskEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schedule_task_id_gen")
    @SequenceGenerator(name = "schedule_task_id_gen", sequenceName = "schedule_task_id_seq", allocationSize = 1)
    @Column(nullable = false)
    val id: Long,

    @Size(max = 1024)
    @Column(nullable = false, length = 1024)
    val clazzPath: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_lock_id")
    val taskLockEntity: TaskLockEntity? = null
)