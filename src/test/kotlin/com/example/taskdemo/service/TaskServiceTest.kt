package com.example.taskdemo.service

internal class TaskServiceTest {

//    private val taskGroupService = TaskGroupService()
//    private val hour1 = Duration.ofHours(1)

//    @Test
//    fun testTaskDelay() {
//        val taskConfig5Delay = TaskConfig.Builder()
//            .withTaskSchedules(listOf(TaskSchedule.fromFixedDelay(Duration.ofSeconds(5))))
//            .build()
//        val taskConfig7Delay = TaskConfig.Builder()
//            .withTaskSchedules(listOf(TaskSchedule.fromFixedDelay(Duration.ofSeconds(7))))
//            .build()
//        val taskConfig10Delay = TaskConfig.Builder()
//            .withTaskSchedules(listOf(TaskSchedule.fromFixedDelay(Duration.ofSeconds(10))))
//            .build()
//
//        taskService.addSchedule(TaskImpl("Task 5s delay"), taskConfig5Delay)
//        taskService.addSchedule(TaskImpl("Task 7s delay"), taskConfig7Delay)
//        taskService.addSchedule(TaskImpl("Task 10s delay"), taskConfig10Delay)
//
//        Thread.sleep(hour1)
//    }
//
//    @Test
//    fun testTaskRate() {
//        val taskConfig5Rate = TaskConfig.Builder()
//            .withTaskSchedules(listOf(TaskSchedule.fromFixedRate(Duration.ofSeconds(5))))
//            .build()
//        val taskConfig7Rate = TaskConfig.Builder()
//            .withTaskSchedules(listOf(TaskSchedule.fromFixedRate(Duration.ofSeconds(7))))
//            .build()
//        val taskConfig10Rate = TaskConfig.Builder()
//            .withTaskSchedules(listOf(TaskSchedule.fromFixedRate(Duration.ofSeconds(10))))
//            .build()
//
//        taskService.addSchedule(TaskImpl("Task 5s rate"), taskConfig5Rate)
//        taskService.addSchedule(TaskImpl("Task 7s rate"), taskConfig7Rate)
//        taskService.addSchedule(TaskImpl("Task 10s rate"), taskConfig10Rate)
//
//        Thread.sleep(hour1)
//    }
//
//    @Test
//    fun testTaskCron() {
//        val taskConfig5s = TaskConfig.Builder()
//            .withTaskSchedules(listOf(getTaskScheduleEvery(5)))
//            .build()
//        val taskConfig7s = TaskConfig.Builder()
//            .withTaskSchedules(listOf(getTaskScheduleEvery(7)))
//            .build()
//        val taskConfig10sHeavy = TaskConfig.Builder()
//            .withTaskSchedules(listOf(getTaskScheduleEvery(10)))
//            .withHeavy(true)
//            .build()
//        val taskConfig8sHeavy = TaskConfig.Builder()
//            .withTaskSchedules(listOf(getTaskScheduleEvery(8)))
//            .withHeavy(true)
//            .build()
//
//        taskService.addSchedule(TaskImpl("Task 5s cron"), taskConfig5s)
//        taskService.addSchedule(TaskImpl("Task 7s cron"), taskConfig7s)
//        taskService.addSchedule(TaskImpl("Task 8s cron heavy"), taskConfig8sHeavy)
//        taskService.addSchedule(TaskImpl("Task 10s cron heavy"), taskConfig10sHeavy)
//
//        Thread.sleep(hour1)
//    }
//
//    @Test
//    fun testTaskQueue() {
//        taskService.stopQueue()
//
//        taskService.addQueue(TaskImpl("Task linked 1"))
//        taskService.addQueue(TaskImpl("Task linked 2"))
//        taskService.addQueue(TaskImpl("Task linked 3"))
//
//        Thread.sleep(Duration.ofSeconds(5))
//        taskService.startQueue()
//
//        Thread.sleep(hour1)
//    }
//
//    @Test
//    fun testTaskScheduleHeavyAndPriority() {
//        val taskConfig5sHeavy = TaskConfig.Builder()
//            .withTaskSchedules(listOf(getTaskScheduleEvery(5)))
//            .withHeavy(true)
//            .build()
//        val taskConfig5sPriority = TaskConfig.Builder()
//            .withTaskSchedules(listOf(getTaskScheduleEvery(5)))
//            .withPriority(100)
//            .build()
//        val taskConfig5s = TaskConfig.Builder()
//            .withTaskSchedules(listOf(getTaskScheduleEvery(5)))
//            .build()
//        val taskConfig7sHeavy = TaskConfig.Builder()
//        .withTaskSchedules(listOf(getTaskScheduleEvery(7)))
//            .withHeavy(true)
//            .build()
//
//        taskService.stopSchedule()
//
//        taskService.addSchedule(TaskImpl("Task 5s cron heavy"), taskConfig5sHeavy)
//        taskService.addSchedule(TaskImpl("Task 5s cron"), taskConfig5s)
//        taskService.addSchedule(TaskImpl("Task 7s cron heavy"), taskConfig7sHeavy)
//        taskService.addSchedule(TaskImpl("Task 5s cron priority"), taskConfig5sPriority)
//
//        Thread.sleep(Duration.ofSeconds(30))
//        taskService.startSchedule()
//
//        Thread.sleep(hour1)
//    }
//
//    @Test
//    fun testDaemonTask() {
//        taskService.stopDaemon()
//
//        taskService.addDaemon(TaskImpl("Daemon 1"))
//        taskService.addDaemon(TaskImpl("Daemon 2"))
//        taskService.addDaemon(TaskImpl("Daemon 3"))
//
//        Thread.sleep(Duration.ofSeconds(5))
//        taskService.startDaemon()
//
//        Thread.sleep(hour1)
//    }
//
//    private fun getTaskScheduleEvery(time: Int): TaskSchedule {
//        return TaskSchedule.fromCron(
//            CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
//                .withSecond(FieldExpressionFactory.every(time))
//                .instance()
//        )
//    }
}