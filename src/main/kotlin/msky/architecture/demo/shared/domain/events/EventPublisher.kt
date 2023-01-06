package msky.architecture.demo.shared.domain.events

interface EventPublisher {
    fun publish(event: DomainEvent)
}