package msky.architecture.demo.module.infra.mongo

import msky.architecture.demo.module.domain.impl.SampleDomainClass
import msky.architecture.demo.module.domain.impl.SampleRepository
import org.springframework.stereotype.Repository

@Repository
class SampleMongoRepository : SampleRepository {
    override fun save(importantBusinessEntity: SampleDomainClass): SampleDomainClass {
        TODO("Save importantBusinessEntity in MongoDB")
    }
}