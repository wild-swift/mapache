package name.wildswift.mapache.generator.generatemodel

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

data class GenerateModel(
        val baseEventClass: ClassName,
        val events: List<EventDefinition>,

        val baseStateWrappersClass: ClassName,
        val states: List<StateDefinition>,
        val dependencySource: TypeName,
        val buildConfigClass: ClassName,

        val baseTransitionClass: ClassName,
        val transitionsFactoryClass: ClassName,
        val emptyTransitionClass: ClassName,
        val defaultTransitionClass: ClassName,
        val transitions: List<TransitionDefinition>,

        val smUtilityClass: ClassName,
        val viewContentMetaSourceClass: ClassName,
        val viewContents: List<ViewContentDefinition>,
        val layers: List<LayerDefinition>
)