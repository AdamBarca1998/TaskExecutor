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
        value = "SELECT qt.id, qt.clazz, qt.state, qt.created_at, qt.created_by, qt.owned_by, qt.result, qt.task_lock_id " +
                "FROM queue_task AS qt " +
                "LEFT JOIN task_lock AS tl ON tl.id = qt.task_lock_id " +
                "WHERE tl.cluster_name = :clusterName " +
                "AND qt.state NOT IN :#{#states.![name()]} " +
                "ORDER BY tl.lock_until DESC",
        nativeQuery = true
    )
    fun findAllByStateNotInAndClusterName(states: List<QueueTaskState>, clusterName: String): List<QueueTaskEntity>

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE task_lock AS tl " +
                "SET lock_until = NOW() + MAKE_INTERVAL(mins => :minutes), locked_at = NOW() " +
                "FROM queue_task AS qt " +
                "WHERE qt.task_lock_id = tl.id " +
                "AND qt.id = :id",
        nativeQuery = true
    )
    fun refreshLockByTaskId(id: Long, minutes: Int): Int

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE queue_task " +
                "SET state = :#{#state.name()} " +
                "WHERE id = :id " +
                "AND state NOT IN :#{#finishedStates.![name()]} ",
        nativeQuery = true
    )
    fun updateStateById(id: Long, state: QueueTaskState, finishedStates: List<QueueTaskState>): Int

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE queue_task " +
                "SET state = :#{#state.name()}, result = :result " +
                "WHERE id = :id",
        nativeQuery = true
    )
    fun updateStateAndResultById(id: Long, state: QueueTaskState, result: String): Int

    @Query(
        value = "SELECT qt.id, qt.clazz, qt.state, qt.created_at, qt.created_by, qt.owned_by, qt.result, qt.task_lock_id " +
                "FROM queue_task AS qt " +
                "LEFT JOIN task_lock AS tl ON tl.id = qt.task_lock_id " +
                "WHERE tl.locked_by = :appId AND tl.cluster_name = :clusterName " +
                "ORDER BY tl.lock_until DESC " +
                "LIMIT 1",
        nativeQuery = true
    )
    fun getNewestLocked(appId: String, clusterName: String): QueueTaskEntity
}