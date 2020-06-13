package name.wildswift.mapache.generator.codegen

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.squareup.javapoet.*
import name.wildswift.mapache.generator.generatemodel.Action
import name.wildswift.mapache.generator.generatemodel.State
import name.wildswift.mapache.generator.genericWildcard
import name.wildswift.mapache.generator.mStateTypeName
import name.wildswift.mapache.generator.navigatableTypeName
import name.wildswift.mapache.generator.viewSetTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier


class StatesWrapperGenerator(
        private val prefix: String,
        private val packageName: String,
        private val processingEnv: ProcessingEnvironment,
        modulePackageName: String,
        private val dependencySource: TypeName,
        private val actionBaseType: TypeName,
        private val actionTypes: Map<Action,TypeName>,
        private val states: List<State>
) {

    val baseTypeName = ClassName.get(packageName, "${prefix}MState")
    val statesNames = states.map { state -> state to ClassName.get(packageName, "${state.name.split(".").last()}Wrapper") }.toMap()


    private val moduleBuildConfig = ClassName.get(modulePackageName, "BuildConfig")

    @SuppressWarnings("DefaultLocale")
    fun generateAll() {
        val wrappedTypeVarible = TypeVariableName.get("MS", ParameterizedTypeName.get(mStateTypeName, actionBaseType, genericWildcard, dependencySource))
        val getWrappedMethod = MethodSpec.methodBuilder("getWrapped")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(wrappedTypeVarible)
                .build()

        val baseClassTypeSpec = TypeSpec
                .classBuilder(baseTypeName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addTypeVariable(wrappedTypeVarible)
                .addSuperinterface(ParameterizedTypeName.get(mStateTypeName, actionBaseType, viewSetTypeName, dependencySource))
                .addSuperinterface(ParameterizedTypeName.get(navigatableTypeName, actionBaseType, dependencySource, ParameterizedTypeName.get(baseTypeName, genericWildcard)))
                .addMethod(getWrappedMethod)
                .build()

        processingEnv.filer.createSourceFile(baseTypeName.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(packageName, baseClassTypeSpec)
                            .build()
                            .writeTo(fileWriter)
                }
        states.forEach { state ->
            val currentStateWrapperName = statesNames[state] ?: error("Internal error")
            val currentStateName = ClassName.bestGuess(state.name)
            val wrappedField = FieldSpec.builder(currentStateName, "wrappedObj").addModifiers(Modifier.PRIVATE, Modifier.FINAL).build()

            val stateWrapperTypeSpecBuilder = TypeSpec
                    .classBuilder(currentStateWrapperName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .superclass(ParameterizedTypeName.get(baseTypeName, currentStateName))
                    .addField(wrappedField)
                    .addMethod(MethodSpec.methodBuilder(getWrappedMethod.name)
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override::class.java)
                            .returns(currentStateName)
                            .addStatement("return \$N", wrappedField)
                            .build()
                    )
                    .addMethod(MethodSpec.methodBuilder("getNextState")
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override::class.java)
                            .addAnnotation(Nullable::class.java)
                            .addParameter(ParameterSpec.builder(actionBaseType, "e").addAnnotation(NonNull::class.java).build())
                            .returns(baseTypeName)
                            .apply {
                                state.movements?.forEach { (action, endState, _) ->
                                    val parametersSting = endState.parameters.orEmpty().joinToString { "((\$1T)e).get${it.name.capitalize()}()" }
                                    addStatement("if (e instanceof \$1T) return \$2T.${createInstanceMethodName}($parametersSting)", actionTypes[action], statesNames[endState])
                                }
                                addStatement("if (\$1T.DEBUG) throw new \$2T(\"Unable to process event \" + e.getClass().getSimpleName())", moduleBuildConfig, ClassName.get(IllegalStateException::class.java))
                                addStatement("return null")
                            }
                            .build()
                    )

            processingEnv.filer.createSourceFile(currentStateWrapperName.canonicalName())
                    .openWriter()
                    .use { fileWriter ->
                        JavaFile.builder(packageName, stateWrapperTypeSpecBuilder.build())
                                .build()
                                .writeTo(fileWriter)
                    }

        }
    }

    companion object {
        @JvmStatic
        private val createInstanceMethodName = "newInstance"
    }
}