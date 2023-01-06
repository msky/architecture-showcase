package msky.architecture.demo.architecture

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.base.DescribedPredicate.*
import com.tngtech.archunit.core.domain.JavaAccess
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClass.Predicates.*
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*
import msky.architecture.demo.shared.domain.events.DomainEvent
import org.springframework.context.event.EventListener
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@AnalyzeClasses(packages = ["msky.architecture.demo"])
class ArchitectureSpec {

    @ArchTest
    val spring_should_not_be_used_in_domain_except_for_facade_and_configuration =
        noClasses().that().resideInAPackage("..domain..")
            .and(areNotFacadeConfiguration())
            .and(areNotModuleFacade())
            .should()
            .dependOnClassesThat().resideInAPackage("..org.springframework..")

    @ArchTest
    val module_facade_should_be_transactional =
        classes().that(areModuleFacade())
            .should()
            .beAnnotatedWith(Transactional::class.java)

    @ArchTest
    val domain_should_be_separated_from_infranstracture =
        noClasses().that().resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat().resideInAPackage("..infra..")

    @ArchTest
    val commands_should_reside_in_separate_package = classes().that().haveSimpleNameEndingWith("Command")
        .should().resideInAPackage("..domain.commands")

    @ArchTest
    val dtos_should_reside_in_separate_package = classes().that().haveSimpleNameEndingWith("Dto")
        .should().resideInAPackage("..domain.dto")

    @ArchTest
    val events_should_reside_in_separate_package = classes().that().haveSimpleNameEndingWith("Event")
        .should().resideInAPackage("..domain.events")

    @ArchTest
    val queries_should_reside_in_separate_package = classes().that().haveSimpleNameEndingWith("Query")
        .should().resideInAPackage("..domain.queries")

    @ArchTest
    val events_queries_commands_dtos_should_be_immutable = classes().that().resideInAnyPackage(
        "..domain.events", "..domain.queries", "..domain.commands", "..domain.dto"
    ).should().haveOnlyFinalFields().andShould().bePublic()

    @ArchTest
    val only_commands_dtos_events_and_queries_are_part_of_module_public_api =
        methods().that().arePublic().and().areDeclaredInClassesThat(areModuleFacade())
            .should()
            .haveRawReturnType(
                resideInAnyPackage("..domain.dto")
                    .or(isVoid())
                    .or(isOptional())
                    .or(isCollection())
            )
            .andShould().haveRawParameterTypes(
                allElements(
                    resideInAnyPackage(
                        "..domain.events", "..domain.queries", "..domain.commands", "..domain.dto"
                    )
                )
            )

    @ArchTest
    val no_internal_domain_implementation_should_be_called_from_outside_the_module =
        classes().that().resideInAPackage("..domain.impl..")
            .should()
            .onlyBeAccessed()
            .byClassesThat(resideInSameModule())
            .because("Only module API (facade) should be used to interact with it")

    @ArchTest
    val facade_methods_handling_events_should_have_proper_annotation =
        methods().that().arePublic().and().areDeclaredInClassesThat(areModuleFacade())
            .and().haveRawParameterTypes(anyElementThat(assignableTo(DomainEvent::class.java)))
            .should()
            .beAnnotatedWith(EventListener::class.java)

    private fun resideInSameModule() =
        object : DescribedPredicate<JavaClass>("class that call domain internals should reside in the same module") {
            override fun test(input: JavaClass) =
                if (isDomainClass(input)) {
                    shouldBeAccessedFromSamModule(input)
                } else {
                    true
                }

            private fun shouldBeAccessedFromSamModule(input: JavaClass) =
                input.accessesToSelf.all { isFromSameModule(it, input) }

            private fun isInfra(access: JavaAccess<*>) = access.originOwner.packageName.contains("infra")

            private fun isFromSameModule(access: JavaAccess<*>, javaClass: JavaClass): Boolean {

                val accessingClassModule = access.originOwner.packageName.substringBefore(
                    if (isInfra(access)) ".infra" else ".domain"
                )
                val accessedClassModule = javaClass.packageName.substringBefore(".domain")

                return accessedClassModule == accessingClassModule
            }

            private fun isDomainClass(input: JavaClass) =
                input.packageName.contains("domain.impl")


        }

    private fun isVoid() = object : DescribedPredicate<JavaClass>("returned type should be void") {
        override fun test(input: JavaClass) = input.name == "void"
    }

    private fun isOptional() = assignableTo(Optional::class.java)

    private fun isCollection() = assignableTo(Collection::class.java)

    private fun areNotModuleFacade() = not(areModuleFacade())

    private fun areModuleFacade() = simpleNameEndingWith("Facade").and(resideInAPackage("..domain"))

    private fun areNotFacadeConfiguration() = not(simpleNameEndingWith("FacadeConfiguration"))

}