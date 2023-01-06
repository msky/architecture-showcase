package msky.architecture.demo.module.domain.impl

interface SampleRepository {
    fun save(importantBusinessEntity: SampleDomainClass): SampleDomainClass
}
