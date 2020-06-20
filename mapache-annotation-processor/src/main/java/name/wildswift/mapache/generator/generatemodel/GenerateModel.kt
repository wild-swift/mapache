package name.wildswift.mapache.generator.generatemodel

import com.squareup.javapoet.ClassName

data class GenerateModel(
        val eventsBasePackage: String,
        val baseEventClass: ClassName,
        val events: List<EventDefinition>,
        val baseStateWrappersClass: ClassName,
        val states: List<StateDefinition>
)