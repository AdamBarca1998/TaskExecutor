package com.example.taskdemo.repository;

import com.example.taskdemo.model.entities.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface TaskRepository : JpaRepository<TaskEntity, Long> {

    @Transactional
    @Modifying
    @Query(
        value = "INSERT INTO task(clazz, enable, task_lock_id) " +
                "VALUES(:#{#taskEntity.clazz}, true, :#{#taskEntity.taskLockEntity.id})",
        nativeQuery = true
    )
    fun insert(taskEntity: TaskEntity)

    @Query(
        value = "SELECT enable FROM task WHERE clazz = :clazz",
        nativeQuery = true
    )
    fun isEnableByClazz(clazz: String): Boolean
}