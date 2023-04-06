package com.example.taskdemo.repository

import com.example.taskdemo.model.entities.DaemonTaskEntity
import com.example.taskdemo.model.entities.ScheduleTaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface DaemonTaskRepository : JpaRepository<DaemonTaskEntity, Long> {

    @Transactional
    @Modifying
    @Query(
        value = "INSERT INTO daemon_task(clazz_path, task_lock_id, task_context_id) " +
                "VALUES(:#{#daemonTaskEntity.clazzPath}, :#{#daemonTaskEntity.taskLockEntity.id}, :#{#daemonTaskEntity.taskContext.id})" +
                "ON CONFLICT DO NOTHING",
        nativeQuery = true
    )
    fun insertIfNotExists(daemonTaskEntity: DaemonTaskEntity)

    @Query(
        value = "SELECT enable FROM daemon_task WHERE id = :id",
        nativeQuery = true
    )
    fun isEnableById(id: Long): Boolean

    fun findByClazzPath(clazzPath: String): DaemonTaskEntity

    @Query(
        value = "SELECT dt.id, clazz_path, task_lock_id, task_context_id " +
                "FROM daemon_task AS dt " +
                "LEFT JOIN task_lock AS tl ON tl.id = dt.task_lock_id " +
                "WHERE tl.cluster_name = :clusterName ",
        nativeQuery = true
    )
    fun findAllByClusterName(clusterName: String): List<DaemonTaskEntity>
}