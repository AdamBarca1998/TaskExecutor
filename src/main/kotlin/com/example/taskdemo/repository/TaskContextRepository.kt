package com.example.taskdemo.repository

import com.example.taskdemo.model.entities.TaskContextEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TaskContextRepository : JpaRepository<TaskContextEntity, Long> {

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE task_context AS tc " +
                "SET start_date_time = :#{#taskContext.startDateTime}, " +
                "last_execution = :#{#taskContext.lastExecution}, " +
                "last_completion = :#{#taskContext.lastCompletion} " +
                "FROM daemon_task AS dt " +
                "WHERE dt.task_context_id = tc.id " +
                "AND dt.clazz_path = :clazzPath",
        nativeQuery = true
    )
    fun updateByClazzPath(taskContext: TaskContextEntity, clazzPath: String): Int
}