package com.example.taskdemo.repository;

import com.example.taskdemo.model.entities.ScheduleTaskEntity
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
        value = "INSERT INTO schedule_task(clazz_path, task_lock_id, task_context_id) " +
                "VALUES(:#{#scheduleTaskEntity.clazzPath}, :#{#scheduleTaskEntity.taskLockEntity.id}, :#{#scheduleTaskEntity.taskContext.id}) " +
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
        value = "SELECT st.id, clazz_path, task_lock_id, task_context_id " +
                "FROM schedule_task AS st " +
                "LEFT JOIN task_lock AS tl ON tl.id = st.task_lock_id " +
                "WHERE tl.cluster_name = :clusterName ",
        nativeQuery = true
    )
    fun findAllByClusterName(clusterName: String): List<ScheduleTaskEntity>
}