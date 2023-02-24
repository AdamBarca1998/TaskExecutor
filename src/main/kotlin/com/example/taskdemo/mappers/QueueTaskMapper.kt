package com.example.taskdemo.mappers

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.QueueTaskEntity
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.tasks.queues.EmailTaskExample
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.*
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers


private val format = Json {
    serializersModule = SerializersModule {
        polymorphic(Task::class) {
            subclass(EmailTaskExample::class)
        }
    }
}

@Mapper(componentModel = "spring", imports = [TaskLockMapper::class])
interface QueueTaskMapper {

    @Mapping(source = "task", target = "clazz", qualifiedByName = ["serializeTask"])
    @Mapping(source = "task", target = "taskLockEntity", qualifiedByName = ["mapTaskLockEntity"])
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    fun toEntity(task: Task): QueueTaskEntity

    companion object {

        private val taskLockMapper: TaskLockMapper = Mappers.getMapper(TaskLockMapper::class.java)

        @JvmStatic
        @Named("serializeTask")
        fun serializeTask(task: Task): String {
            return format.encodeToString(task)
        }

        @JvmStatic
        @Named("mapTaskLockEntity")
        fun mapTaskLockEntity(task: Task): TaskLockEntity {
            return taskLockMapper.toEntity(task)
        }
    }
}