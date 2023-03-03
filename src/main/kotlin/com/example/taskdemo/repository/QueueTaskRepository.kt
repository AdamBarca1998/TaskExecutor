package com.example.taskdemo.repository

import com.example.taskdemo.enums.QueueTaskState
import com.example.taskdemo.model.entities.QueueTaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface QueueTaskRepository : JpaRepository<QueueTaskEntity, Long> {

    @Query(
        value = "SELECT q.id, clazz, state, created_at, created_by, owned_by, result, task_lock_id " +
                "FROM queue_task AS q " +
                "LEFT JOIN task_lock AS tl ON tl.id = q.task_lock_id " +
                "WHERE lock_until < NOW() - MAKE_INTERVAL(mins => :minutes) " +
                "AND state != 'FINISHED' " +
                "ORDER BY created_at",
        nativeQuery = true
    )
    fun findExpired(minutes: Int): List<QueueTaskEntity>

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE task_lock AS tl " +
                "SET lock_until = NOW() + MAKE_INTERVAL(mins => :minutes), locked_at = NOW() " +
                "FROM queue_task AS qt " +
                "WHERE qt.task_lock_id = tl.id " +
                "AND qt.id in :ids",
        nativeQuery = true
    )
    fun refreshTasksByIds(ids: List<Long>, minutes: Int): Int

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE queue_task " +
                "SET state = :#{#state.name()} " +
                "WHERE id = :id",
        nativeQuery = true
    )
    fun updateStateById(id: Long, state: QueueTaskState): Int

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE queue_task " +
                "SET state = :#{#state.name()}, result = :result " +
                "WHERE id = :id",
        nativeQuery = true
    )
    fun updateStateAndResultById(id: Long, state: QueueTaskState, result: String): Int
}