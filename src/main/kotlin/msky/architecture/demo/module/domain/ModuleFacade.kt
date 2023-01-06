package msky.architecture.demo.module.domain

import msky.architecture.demo.module.domain.commands.SampleCommand
import msky.architecture.demo.module.domain.dto.SampleResponseDto
import msky.architecture.demo.module.domain.events.SampleEvent
import msky.architecture.demo.module.domain.impl.SampleDomainClass
import msky.architecture.demo.module.domain.impl.SampleRepository
import msky.architecture.demo.module.domain.queries.SampleQuery
import msky.architecture.demo.othermodule.domain.events.EventFromOtherModule
import msky.architecture.demo.shared.domain.events.EventPublisher
import org.springframework.context.event.EventListener
import org.springframework.transaction.annotation.Transactional

@Transactional
class ModuleFacade(
    private val eventBus: EventPublisher,
    private val sampleRepository: SampleRepository,
) {
    fun execute(command: SampleCommand) {
        sampleRepository.save(SampleDomainClass())
            .let { eventBus.publish(SampleEvent()) }
    }

    fun execute(query: SampleQuery) = SampleResponseDto()

    @EventListener
    fun handle(event: EventFromOtherModule) {
        TODO("Build some read model or trigger some processing")
    }
}