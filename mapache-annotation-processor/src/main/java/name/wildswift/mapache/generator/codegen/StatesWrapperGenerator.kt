package name.wildswift.mapache.generator.codegen

import com.squareup.javapoet.*
import com.squareup.javapoet.WildcardTypeName
import name.wildswift.mapache.events.Event
import name.wildswift.mapache.generator.generatemodel.State
import name.wildswift.mapache.generator.genericWildcard
import name.wildswift.mapache.generator.mStateTypeName
import name.wildswift.mapache.generator.navigatableTypeName
import name.wildswift.mapache.generator.viewSetTypeName
import name.wildswift.mapache.graph.Navigatable
import name.wildswift.mapache.states.MState
import name.wildswift.mapache.viewsets.ViewSet
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier


class StatesWrapperGenerator(
        private val prefix: String,
        private val packageName: String,
        private val processingEnv: ProcessingEnvironment,
        private val dependencySource: TypeName,
        private val actionsType: TypeName,
        private val states: List<State>
) {

    val baseTypeName = ClassName.get(packageName, "${prefix}MState")


    fun generateAll() {
        val wrappedTypeVarible = TypeVariableName.get("MS", ParameterizedTypeName.get(mStateTypeName, actionsType, genericWildcard, dependencySource))
        val getWrappedMethod = MethodSpec.methodBuilder("getWrapped")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(wrappedTypeVarible)
                .build()

        val baseClassTypeSpec = TypeSpec
                .classBuilder(baseTypeName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addTypeVariable(wrappedTypeVarible)
                .addSuperinterface(ParameterizedTypeName.get(mStateTypeName, actionsType, viewSetTypeName, dependencySource))
                .addSuperinterface(ParameterizedTypeName.get(navigatableTypeName, actionsType, dependencySource, ParameterizedTypeName.get(baseTypeName, genericWildcard)))
                .addMethod(getWrappedMethod)
                .build()

        processingEnv.filer.createSourceFile(baseTypeName.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(packageName, baseClassTypeSpec)
                            .build()
                            .writeTo(fileWriter)
                }

//        implements Navigatable<AlarmClockEvent, ServicesRepository, AlarmClockScreenState<?>> {
//            public abstract MS getWrapped();
//        }

    }
}