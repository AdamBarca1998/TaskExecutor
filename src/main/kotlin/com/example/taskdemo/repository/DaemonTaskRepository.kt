package com.example.taskdemo.repository

import com.example.taskdemo.model.entities.DaemonTaskEntity
import com.example.taskdemo.model.entities.ScheduleTaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface DaemonTaskRepository : JpaRepository<ScheduleTaskEntity, Long> {

    @Transactional
    @Modifying
    @Query(
        value = "INSERT INTO daemon_task(clazz_path, task_lock_id) " +
                "VALUES(:#{#daemonTaskEntity.clazzPath}, :#{#daemonTaskEntity.taskLockEntity.id})",
        nativeQuery = true
    )
    fun insert(daemonTaskEntity: DaemonTaskEntity)

    @Query(
        value = "SELECT enable FROM daemon_task WHERE clazz_path = :clazzPath",
        nativeQuery = true
    )
    fun isEnableByClazzPath(clazzPath: String): Boolean
}