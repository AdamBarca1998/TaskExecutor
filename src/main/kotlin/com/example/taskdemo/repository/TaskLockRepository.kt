package com.example.taskdemo.repository

import com.example.taskdemo.model.entities.TaskLockEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface TaskLockRepository : JpaRepository<TaskLockEntity, Long> {

    @Transactional
    @Query(
        value = "SELECT id, name, lock_until, locked_at, locked_by " +
                "FROM barca.task_lock " +
                "WHERE lock_until < NOW() - MAKE_INTERVAL(mins => :minutes)",
        nativeQuery = true
    )
    fun findExpiredLocks(minutes: Int): List<TaskLockEntity>


    fun findByName(name: String): TaskLockEntity?

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE task_lock " +
                "SET lock_until = NOW() + MAKE_INTERVAL(mins => :minutes), locked_at = NOW(), locked_by = :appId " +
                "WHERE lock_until < NOW() - make_interval(mins => :minutes) AND id = :id",
        nativeQuery = true
    )
    fun refreshLockById(id: Long, minutes: Int, appId: String): Int
}