package name.wildswift.mapache.generator.codegen

import androidx.annotation.NonNull
import com.squareup.javapoet.*
import name.wildswift.mapache.generator.*
import name.wildswift.mapache.generator.codegen.GenerationConstants.getWrappedMethodName
import name.wildswift.mapache.generator.codegen.GenerationConstants.initWrappedMethodName
import name.wildswift.mapache.generator.generatemodel.TransitionDefinition
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier


class TransitionsWrapperGenerator(
        private val packageName: String,
        private val baseTypeName: ClassName,
        private val emptyWrapperTypeName: ClassName,
        private val baseActionsType: ClassName,
        private val baseStatesType: ClassName,
        private val dependencySourceType: TypeName,
        private val transitions: List<TransitionDefinition>,
        private val processingEnv: ProcessingEnvironment
) {



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
            if (emptyTransitionTypeName == transitionDesc.typeName) return@forEach

            val stateTransitionRootViewType = viewGroupClass

            val transitionWrapperTypeName = transitionDesc.wrapperTypeName

            val transitionImplClassName = transitionDesc.typeName


            val transitionWrapperTypeSpecBuilder = TypeSpec.classBuilder(transitionWrapperTypeName)
                    .superclass(ParameterizedTypeName.get(baseTypeName, stateTransitionRootViewType,
                            transitionDesc.beginViewSetClass, transitionDesc.beginStateClass, transitionDesc.beginStateWrapperClass,
                            transitionDesc.endViewSetClass, transitionDesc.endStateClass, transitionDesc.endStateWrapperClass
                    ))
                    .addMethod(MethodSpec.constructorBuilder()
                            .addParameter(ParameterSpec.builder(transitionDesc.beginStateWrapperClass, "from").addAnnotation(NonNull::class.java).build())
                            .addParameter(ParameterSpec.builder(transitionDesc.endStateWrapperClass, "to").addAnnotation(NonNull::class.java).build())
                            .addStatement("super(from, to)")
                            .build()
                    )
                    .addMethod(MethodSpec.methodBuilder("buildWrapped")
                            .addParameter(ParameterSpec.builder(transitionDesc.beginStateClass, "from").addAnnotation(NonNull::class.java).build())
                            .addParameter(ParameterSpec.builder(transitionDesc.endStateClass, "to").addAnnotation(NonNull::class.java).build())
                            .returns(ParameterizedTypeName.get(stateTransitionTypeName, baseActionsType, transitionDesc.beginViewSetClass, transitionDesc.endViewSetClass, stateTransitionRootViewType, dependencySourceType))
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