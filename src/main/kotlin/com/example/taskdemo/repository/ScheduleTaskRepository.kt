package com.example.taskdemo.repository;

import com.example.taskdemo.model.entities.ScheduleTaskEntity
import com.example.taskdemo.model.entities.TaskContext
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface ScheduleTaskRepository : JpaRepository<ScheduleTaskEntity, Long> {

    @Transactional
    @Modifying
    @Query(
        value = "INSERT INTO schedule_task(clazz_path, enable, task_lock_id, last_execution, last_completion, next_execution) " +
                "VALUES(:#{#scheduleTaskEntity.clazzPath}, true, :#{#scheduleTaskEntity.taskLockEntity.id}, " +
                ":#{#scheduleTaskEntity.taskContext.lastExecution}, " +
                ":#{#scheduleTaskEntity.taskContext.lastCompletion}, :#{#scheduleTaskEntity.taskContext.nextExecution}) " +
                "ON CONFLICT DO NOTHING",
        nativeQuery = true
    )
    fun insertIfNotExists(scheduleTaskEntity: ScheduleTaskEntity)

    @Query(
        value = "SELECT enable FROM schedule_task WHERE id = :id",
        nativeQuery = true
    )
    fun isEnableById(id: Long): Boolean

    fun findByClazzPath(clazzPath: String): ScheduleTaskEntity

    @Query(
        value = "SELECT st.id, clazz_path, enable, task_lock_id, last_execution, last_completion, next_execution " +
                "FROM schedule_task AS st " +
                "LEFT JOIN task_lock AS tl ON tl.id = st.task_lock_id " +
                "WHERE tl.cluster_name = :clusterName ",
        nativeQuery = true
    )
    fun findAllByClusterName(clusterName: String): List<ScheduleTaskEntity>

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE schedule_task " +
                "SET last_execution = :#{#context.lastExecution}, " +
                "last_completion = :#{#context.lastCompletion}, next_execution = :#{#context.nextExecution} " +
                "WHERE schedule_task.id = :id",
        nativeQuery = true
    )
    fun updateContextById(id: Long, context: TaskContext): Int
}