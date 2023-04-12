package com.example.taskdemo.model.entities

import com.example.taskdemo.enums.TaskState
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import java.time.ZonedDateTime

@Entity
@Table(name = "task_log")
open class TaskLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @NotNull
    @Column(name = "start_date_time", nullable = false)
    open var startDateTime: ZonedDateTime = ZonedDateTime.now()

    @Column(name = "finish_date_time")
    open var finishDateTime: ZonedDateTime? = null

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 16)
    open var state: TaskState = TaskState.RUNNING

    @NotNull
    @Column(name = "result", nullable = false, length = Integer.MAX_VALUE)
    open var result: String = ""

    @Column(name = "queue_task_id")
    open var queueTaskId: Long? = null

    @Column(name = "schedule_task_id")
    open var scheduleTaskId: Long? = null

    @Column(name = "daemon_task_id")
    open var daemonTaskId: Long? = null
}