package com.example.taskdemo.repository

import com.example.taskdemo.enums.TaskState
import com.example.taskdemo.model.entities.TaskLockEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TaskLockRepository : JpaRepository<TaskLockEntity, Long> {

    @Transactional
    @Modifying
    @Query(
        value = "INSERT INTO task_lock(name, lock_until, locked_at, locked_by, cluster_name) " +
                "VALUES(:#{#taskLockEntity.name}, :#{#taskLockEntity.lockUntil}, :#{#taskLockEntity.lockedAt}, " +
                ":#{#taskLockEntity.lockedBy}, :#{#taskLockEntity.clusterName}) " +
                "ON CONFLICT DO NOTHING",
        nativeQuery = true
    )
    fun insertIfNotExists(taskLockEntity: TaskLockEntity)

    fun findByNameAndClusterName(name: String, clusterName: String): TaskLockEntity

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE task_lock " +
                "SET lock_until = NOW() + MAKE_INTERVAL(mins => :minutes), locked_at = NOW(), locked_by = :appId " +
                "WHERE ( lock_until < NOW() OR locked_by = :appId) " +
                "AND name = :name AND cluster_name = :clusterName",
        nativeQuery = true
    )
    fun tryRefreshLockByNameAndClusterName(name: String, minutes: Int, appId: String, clusterName: String): Int

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE task_lock AS tl1 " +
                "SET lock_until = NOW() + MAKE_INTERVAL(mins => :minutes), locked_at = NOW(), locked_by = :appId " +
                "WHERE tl1.id = (" +
                    "SELECT tl2.id " +
                    "FROM queue_task AS qt2 " +
                    "LEFT JOIN task_lock AS tl2 ON tl2.id = qt2.task_lock_id " +
                    "WHERE tl2.lock_until < NOW()" +
                    "AND qt2.state NOT IN :#{#withoutStates.![name()]} AND tl2.cluster_name = :clusterName " +
                    "ORDER BY tl2.lock_until " +
                    "LIMIT 1" +
                ") ",
        nativeQuery = true
    )
    fun lockOldestExpiredQueueTaskByClusterName(minutes: Int, appId: String, withoutStates: List<TaskState>, clusterName: String): Int
}