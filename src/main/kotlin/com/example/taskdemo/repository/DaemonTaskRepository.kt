package com.example.taskdemo.repository

import com.example.taskdemo.model.entities.DaemonTaskEntity
import com.example.taskdemo.model.entities.ScheduleTaskEntity
import com.example.taskdemo.model.entities.TaskContext
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
        value = "INSERT INTO daemon_task(clazz_path, enable, task_lock_id, last_execution, last_completion, next_execution) " +
                "VALUES(:#{#daemonTaskEntity.clazzPath}, true, :#{#daemonTaskEntity.taskLockEntity.id}, " +
                ":#{#daemonTaskEntity.taskContext.lastExecution}, " +
                ":#{#daemonTaskEntity.taskContext.lastCompletion}, :#{#daemonTaskEntity.taskContext.nextExecution})" +
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
        value = "SELECT dt.id, clazz_path, enable, task_lock_id, last_execution, last_completion, next_execution " +
                "FROM daemon_task AS dt " +
                "LEFT JOIN task_lock AS tl ON tl.id = dt.task_lock_id " +
                "WHERE tl.cluster_name = :clusterName ",
        nativeQuery = true
    )
    fun findAllByClusterName(clusterName: String): List<DaemonTaskEntity>

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE daemon_task " +
                "SET last_execution = :#{#context.lastExecution}, " +
                "last_completion = :#{#context.lastCompletion}, next_execution = :#{#context.nextExecution} " +
                "WHERE daemon_task.id = :id",
        nativeQuery = true
    )
    fun updateContextById(id: Long, context: TaskContext): Int
}