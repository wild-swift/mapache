package name.wildswift.mapache.generator.codegen

import androidx.annotation.NonNull
import com.squareup.javapoet.*
import name.wildswift.mapache.generator.*
import name.wildswift.mapache.generator.codegen.GenerationConstants.getWrappedMethodName
import name.wildswift.mapache.generator.codegen.GenerationConstants.initWrappedMethodName
import name.wildswift.mapache.generator.generatemodel.State
import name.wildswift.mapache.generator.generatemodel.TransitionDesc
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier


class TransitionsWrapperGenerator(
        private val prefix: String,
        private val packageName: String,
        private val processingEnv: ProcessingEnvironment,
        private val baseActionsType: ClassName,
        private val baseStatesType: ClassName,
        private val dependencySourceType: TypeName,
        private val transitions: List<TransitionDesc>,
        private val stateNames: Map<State, ClassName>,
        private val stateWrappersNames: Map<State, ClassName>
) {

    val baseTypeName = ClassName.get(packageName, "${prefix}StateTransition")

    private val navigationContextType = ParameterizedTypeName.get(navigationContextTypeName, baseActionsType, dependencySourceType)
    private val navigationContextParameter = ParameterSpec.builder(navigationContextType, "context").addAnnotation(NonNull::class.java).build()

    fun generateAll() {
        val rootViewType = TypeVariableName.get("VR", viewClass)

        val inViewSetType = TypeVariableName.get("VS_IN", viewSetTypeName)
        val inStateType = TypeVariableName.get("S_IN", ParameterizedTypeName.get(mStateTypeName, baseActionsType, inViewSetType, rootViewType, dependencySourceType))
        val inStateWrapperType = TypeVariableName.get("SS", ParameterizedTypeName.get(baseStatesType, rootViewType, inStateType))

        val outViewSetType = TypeVariableName.get("VS_OUT", viewSetTypeName)
        val outStateType = TypeVariableName.get("S_OUT", ParameterizedTypeName.get(mStateTypeName, baseActionsType, outViewSetType, rootViewType, dependencySourceType))
        val outStateWrapperType = TypeVariableName.get("TS", ParameterizedTypeName.get(baseStatesType, rootViewType, outStateType))

        val wrappedObjectType = ParameterizedTypeName.get(stateTransitionTypeName, baseActionsType, inViewSetType, outViewSetType, rootViewType, dependencySourceType)
        val wrappedField = FieldSpec.builder(wrappedObjectType, "wrappedObj", Modifier.PRIVATE, Modifier.FINAL)
                .build()
        val internalCallbakWrapper = buildCallbackWrapperType(outViewSetType)

        val baseClassTypeSpec = TypeSpec
                .classBuilder(baseTypeName)
                .addTypeVariable(rootViewType)
                .addTypeVariables(listOf(inViewSetType, inStateType, inStateWrapperType))
                .addTypeVariables(listOf(outViewSetType, outStateType, outStateWrapperType))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(ParameterizedTypeName.get(stateTransitionTypeName, baseActionsType, viewSetTypeName, viewSetTypeName, rootViewType, dependencySourceType))
                .addField(wrappedField)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(ParameterSpec.builder(inStateWrapperType, "from").addAnnotation(NonNull::class.java).build())
                        .addParameter(ParameterSpec.builder(outStateWrapperType, "to").addAnnotation(NonNull::class.java).build())
                        .addStatement("super(from, to)")
                        .addStatement("\$N = ${initWrappedMethodName}(from.${getWrappedMethodName}(), to.${getWrappedMethodName}())", wrappedField)
                        .build()
                )
                .addMethod(MethodSpec.methodBuilder(initWrappedMethodName)
                        .addModifiers(Modifier.ABSTRACT)
                        .addParameter(ParameterSpec.builder(inStateType, "from").build())
                        .addParameter(ParameterSpec.builder(outStateType, "to").build())
                        .returns(wrappedObjectType)
                        .build()
                )
                .addMethod(MethodSpec.methodBuilder("execute")
                        .addAnnotation(Override::class.java)
                        .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\"unchecked\"").build())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(navigationContextParameter)
                        .addParameter(ParameterSpec.builder(rootViewType, "rootView").addAnnotation(NonNull::class.java).build())
                        .addParameter(ParameterSpec.builder(viewSetTypeName, "inViews").addAnnotation(NonNull::class.java).build())
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(stateTransitionCallbackTypeName, viewSetTypeName), "callback").addAnnotation(NonNull::class.java).build())
                        .addStatement("\$N.execute(\$N, rootView, (\$T) inViews, new \$N(callback))", wrappedField, navigationContextParameter, inViewSetType, internalCallbakWrapper)
                        .build())
                .addType(internalCallbakWrapper)
                .build()

        processingEnv.filer.createSourceFile(baseTypeName.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(packageName, baseClassTypeSpec)
                            .build()
                            .writeTo(fileWriter)
                }

        transitions.forEach { transitionDesc ->
            if (emptyTransitionTypeName.canonicalName() == transitionDesc.implClass) return@forEach

            val stateTransitionRootViewType = viewGroupClass

            val fromName = transitionDesc.beginState.name.split(".").last().let { if (it.endsWith("State")) it.dropLast("State".length) else it }
            val toName = transitionDesc.endState.name.split(".").last().let { if (it.endsWith("State")) it.dropLast("State".length) else it }
            val transitionWrapperTypeName = ClassName.get(packageName, "${fromName}To${toName}TransitionWrapper")

            val fromViewSetType = processingEnv.elementUtils.getTypeElement(transitionDesc.beginState.name).extractViewSetType()
            val toViewSetType = processingEnv.elementUtils.getTypeElement(transitionDesc.endState.name).extractViewSetType()

            val transitionImplClassName = transitionDesc.implClass.toType()


            val transitionWrapperTypeSpecBuilder = TypeSpec.classBuilder(transitionWrapperTypeName)
                    .superclass(ParameterizedTypeName.get(baseTypeName, stateTransitionRootViewType,
                            fromViewSetType, stateNames[transitionDesc.beginState], stateWrappersNames[transitionDesc.beginState],
                            toViewSetType, stateNames[transitionDesc.endState], stateWrappersNames[transitionDesc.endState]
                    ))
                    .addMethod(MethodSpec.constructorBuilder()
                            .addParameter(ParameterSpec.builder(stateWrappersNames[transitionDesc.beginState], "from").addAnnotation(NonNull::class.java).build())
                            .addParameter(ParameterSpec.builder(stateWrappersNames[transitionDesc.endState], "to").addAnnotation(NonNull::class.java).build())
                            .addStatement("super(from, to)")
                            .build()
                    )
                    .addMethod(MethodSpec.methodBuilder("buildWrapped")
                            .addParameter(ParameterSpec.builder(stateNames[transitionDesc.beginState], "from").addAnnotation(NonNull::class.java).build())
                            .addParameter(ParameterSpec.builder(stateNames[transitionDesc.endState], "to").addAnnotation(NonNull::class.java).build())
                            .returns(ParameterizedTypeName.get(stateTransitionTypeName, baseActionsType, fromViewSetType, toViewSetType, stateTransitionRootViewType, dependencySourceType))
                            .addStatement("return new \$T(from, to)", transitionImplClassName)
                            .build())

            processingEnv.filer.createSourceFile(transitionWrapperTypeName.canonicalName())
                    .openWriter()
                    .use { fileWriter ->
                        JavaFile.builder(packageName, transitionWrapperTypeSpecBuilder.build())
                                .build()
                                .writeTo(fileWriter)
                    }
        }

    }

    private fun buildCallbackWrapperType(viewSetType: TypeVariableName?): TypeSpec? {
        val wrappedField = FieldSpec.builder(ParameterizedTypeName.get(stateTransitionCallbackTypeName, viewSetTypeName), "callback").addModifiers(Modifier.PRIVATE, Modifier.FINAL).addAnnotation(NonNull::class.java).build()
        val wrappedFieldConstructorParameter = ParameterSpec.builder(ParameterizedTypeName.get(stateTransitionCallbackTypeName, viewSetTypeName), "callback").addAnnotation(NonNull::class.java).build()
        val onTransitionEndedName = "onTransitionEnded"
        val currentSetOnTransitionEndedName = ParameterSpec.builder(viewSetType, "currentSet").addAnnotation(NonNull::class.java).build()

        return TypeSpec.classBuilder("TransitionCallbackWrapper")
                .addModifiers(Modifier.PRIVATE)
                .addSuperinterface(ParameterizedTypeName.get(stateTransitionCallbackTypeName, viewSetType))
                .addField(wrappedField)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .addParameter(wrappedFieldConstructorParameter)
                        .addStatement("this.\$N = \$N", wrappedField, wrappedFieldConstructorParameter)
                        .build()
                )
                .addMethod(MethodSpec.methodBuilder(onTransitionEndedName)
                        .addAnnotation(Override::class.java)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(currentSetOnTransitionEndedName)
                        .addStatement("this.\$N.$onTransitionEndedName(\$N)", wrappedField, currentSetOnTransitionEndedName)
                        .build()

                )
                .build()
    }
}