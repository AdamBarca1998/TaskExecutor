package com.example.taskdemo.mappers

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskLockEntity
import java.util.*
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named

@Mapper(componentModel = "spring")
interface TaskLockMapper {

    @Mapping(source = "task", target = "name", qualifiedByName = ["mapName"])
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lockUntil", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "lockedBy", ignore = true)
    @Mapping(target = "clusterName", ignore = true)
    fun toEntity(task: Task): TaskLockEntity

    companion object {

        @JvmStatic
        @Named("mapName")
        fun mapName(task: Task): String {
            return task.javaClass.name + " - " + UUID.randomUUID().toString()
        }
    }
}