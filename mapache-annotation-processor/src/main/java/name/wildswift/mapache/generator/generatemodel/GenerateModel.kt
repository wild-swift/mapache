package name.wildswift.mapache.generator.generatemodel

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

data class GenerateModel(
        val eventsBasePackage: String,
        val baseEventClass: ClassName,
        val events: List<EventDefinition>,
        val statesBasePackage: String,
        val baseStateWrappersClass: ClassName,
        val states: List<StateDefinition>,
        val dependencySource: TypeName,
        val buildConfigClass: ClassName,

        val transitionsBasePackage: String,
        val baseTransitionClass: ClassName,
        val emptyTransitionClass: ClassName,
        val transitions: List<TransitionDefinition>
)