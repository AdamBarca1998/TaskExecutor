package com.example.taskdemo.repository;

import com.example.taskdemo.model.entities.ScheduleTaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface ScheduleTaskRepository : JpaRepository<ScheduleTaskEntity, Long> {

    @Transactional
    @Modifying
    @Query(
        value = "INSERT INTO schedule_task(clazz, task_lock_id) " +
                "VALUES(:#{#scheduleTaskEntity.clazz}, :#{#scheduleTaskEntity.taskLockEntity.id})",
        nativeQuery = true
    )
    fun insert(scheduleTaskEntity: ScheduleTaskEntity)

    @Query(
        value = "SELECT enable FROM schedule_task WHERE clazz = :clazz",
        nativeQuery = true
    )
    fun isEnableByClazz(clazz: String): Boolean
}