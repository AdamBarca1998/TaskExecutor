package com.example.taskdemo.taskgroup

import com.example.taskdemo.service.ScheduleTaskService

class QueueTaskGroup(
    private val scheduleTaskService: ScheduleTaskService,
) : SerializedTaskGroup(
    scheduleTaskService
) {

}