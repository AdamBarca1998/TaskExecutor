package com.example.taskdemo.mappers

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.QueueTaskEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named

@Mapper(componentModel = "spring")
interface QueueTaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "task", target = "clazz", qualifiedByName = ["serializeTask"])
    fun toEntity(task: Task): QueueTaskEntity

    @Named("serializeTask")
    fun serializeTask(task: Task): String {
        val a = Json.encodeToString(task)

        return "asdsad"
    }
}