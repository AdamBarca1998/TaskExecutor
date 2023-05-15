package com.example.taskdemo.service

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.springframework.stereotype.Service


@Service
class MetricService(
    prometheusMeterRegistry: PrometheusMeterRegistry
) {

    private val composite = CompositeMeterRegistry()
        .add(prometheusMeterRegistry)
    private val counter: Counter = composite.counter("abc.counter", "tag", "queueTS")

    fun inc() {
        counter.increment()
    }
}