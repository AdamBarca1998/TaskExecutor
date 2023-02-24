package com.example.taskdemo.repository

import com.example.taskdemo.model.entities.QueueTaskEntity
import org.springframework.data.jpa.repository.JpaRepository

interface QueueTaskRepository : JpaRepository<QueueTaskEntity, Long> {

//    @Transactional
//    @Modifying
//    @Query(
//        value = "INSERT INTO queue_task(clazz) " +
//                "VALUES(:#{#queueTaskEntity.clazz})",
//        nativeQuery = true
//    )
//    fun insert(queueTaskEntity: QueueTaskEntity)
}