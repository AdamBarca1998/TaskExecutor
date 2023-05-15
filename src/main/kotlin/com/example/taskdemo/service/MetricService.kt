package com.example.taskdemo.service

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.springframework.stereotype.Service


@Service
class MetricService(
    prometheusMeterRegistry: PrometheusMeterRegistry
) {

    private val registry = SimpleMeterRegistry()
    private val counter: Counter = registry.counter("abc.counter", "tag", "queueTS")

    fun inc() {
        counter.increment()
    }
}