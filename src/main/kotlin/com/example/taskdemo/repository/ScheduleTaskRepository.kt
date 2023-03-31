package com.example.taskdemo.repository;

import com.example.taskdemo.model.entities.ScheduleTaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface ScheduleTaskRepository : JpaRepository<ScheduleTaskEntity, Long> {

    @Transactional
    @Modifying
    @Query(
        value = "INSERT INTO schedule_task(clazz_path, task_lock_id) " +
                "VALUES(:#{#scheduleTaskEntity.clazzPath}, :#{#scheduleTaskEntity.taskLockEntity.id}) " +
                "ON CONFLICT DO NOTHING",
        nativeQuery = true
    )
    fun insertIfNotExists(scheduleTaskEntity: ScheduleTaskEntity)

    @Query(
        value = "SELECT enable FROM schedule_task WHERE id = :id",
        nativeQuery = true
    )
    fun isEnableById(id: Long): Boolean

    fun findByClazzPath(clazzPath: String): ScheduleTaskEntity
}