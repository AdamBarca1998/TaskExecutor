package com.example.taskdemo.repository

import com.example.taskdemo.enums.QueueTaskState
import com.example.taskdemo.model.entities.TaskLockEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TaskLockRepository : JpaRepository<TaskLockEntity, Long> {

    fun findByName(name: String): TaskLockEntity

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE task_lock " +
                "SET lock_until = NOW() + MAKE_INTERVAL(mins => :minutes), locked_at = NOW(), locked_by = :appId " +
                "WHERE ( lock_until < NOW() - MAKE_INTERVAL(mins => :minutes) OR locked_by = :appId) " +
                "AND name = :name",
        nativeQuery = true
    )
    fun tryRefreshLockByName(name: String, minutes: Int, appId: String): Int

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE task_lock AS tl1 " +
                "SET lock_until = NOW() + MAKE_INTERVAL(mins => :minutes), locked_at = NOW(), locked_by = :appId " +
                "WHERE tl1.id = (" +
                    "SELECT tl2.id " +
                    "FROM queue_task AS qt2 " +
                    "LEFT JOIN task_lock AS tl2 ON tl2.id = qt2.task_lock_id " +
                    "WHERE tl2.lock_until < NOW() - MAKE_INTERVAL(mins => :minutes) " +
                    "AND qt2.state NOT IN :#{#withoutStates.![name()]} " +
                    "ORDER BY tl2.lock_until " +
                    "LIMIT 1" +
                ") ",
        nativeQuery = true
    )
    fun lockOldestExpiredQueue(minutes: Int, appId: String, withoutStates: List<QueueTaskState>): Int
}