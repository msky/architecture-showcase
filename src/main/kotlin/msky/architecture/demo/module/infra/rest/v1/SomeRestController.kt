package msky.architecture.demo.module.infra.rest.v1

import msky.architecture.demo.module.domain.ModuleFacade
import msky.architecture.demo.module.domain.dto.SampleResponseDto
import msky.architecture.demo.module.domain.queries.SampleQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/showcase")
class SomeRestController(private val facade: ModuleFacade) {

    @GetMapping
    fun showSomething(query: SampleQuery): ResponseEntity<SampleResponseDto> =
        facade.execute(query).let { ResponseEntity.ok(it) }

}