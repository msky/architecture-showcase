package msky.architecture.demo.shared.infra.events

import msky.architecture.demo.shared.domain.events.DomainEvent
import msky.architecture.demo.shared.domain.events.EventPublisher

class SpringEventBus: EventPublisher {
    override fun publish(event: DomainEvent) {
        TODO("Publish event to Spring Event Bus")
    }
}