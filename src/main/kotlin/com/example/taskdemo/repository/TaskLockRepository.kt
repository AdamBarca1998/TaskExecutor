package com.example.taskdemo.repository

import com.example.taskdemo.model.entities.TaskLockEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface TaskLockRepository : JpaRepository<TaskLockEntity, Long> {

    fun findByName(name: String): TaskLockEntity

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE task_lock " +
                "SET lock_until = NOW() + MAKE_INTERVAL(mins => :minutes), locked_at = NOW(), locked_by = :appId " +
                "WHERE ( lock_until < NOW() - make_interval(mins => :minutes) OR locked_by = :appId) " +
                "AND name = :name",
        nativeQuery = true
    )
    fun tryRefreshLockByName(name: String, minutes: Int, appId: String): Int
}