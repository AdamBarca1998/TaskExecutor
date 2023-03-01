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
                "FROM barca.queue_task AS q " +
                "LEFT JOIN task_lock AS tl ON tl.id = q.task_lock_id " +
                "WHERE lock_until < NOW() - MAKE_INTERVAL(mins => 13) " +
                "AND state != 'FINISHED'",
        nativeQuery = true
    )
    fun findExpired(minutes: Int): List<QueueTaskEntity>

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