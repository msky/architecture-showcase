package msky.architecture.demo.module.domain

import msky.architecture.demo.module.domain.impl.SampleRepository
import msky.architecture.demo.shared.domain.events.EventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ModuleFacadeConfiguration {

    @Bean
    fun moduleFacade(eventPublisher: EventPublisher, sampleRepository: SampleRepository) =
        ModuleFacade(eventPublisher, sampleRepository)
}