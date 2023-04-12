package com.example.taskdemo.repository

import com.example.taskdemo.model.entities.TaskLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskLogRepository : JpaRepository<TaskLogEntity, Long> {
}