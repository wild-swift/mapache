package name.wildswift.mapache.generator.codegen

import androidx.annotation.NonNull
import com.squareup.javapoet.*
import name.wildswift.mapache.generator.*
import name.wildswift.mapache.generator.codegen.GenerationConstants.getWrappedMethodName
import name.wildswift.mapache.generator.codegen.GenerationConstants.initWrappedMethodName
import name.wildswift.mapache.generator.generatemodel.TransitionDefinition
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier


class TransitionsWrapperGenerator(
        private val baseTypeName: ClassName,
        private val emptyWrapperTypeName: ClassName,
        private val defaultWrapperTypeName: ClassName,
        private val transitionsFactoryImplTypeName: ClassName,
        private val baseActionsType: ClassName,
        private val baseStatesType: ClassName,
        private val dependencySourceType: TypeName,
        private val moduleBuildConfig: ClassName,
        private val transitions: List<TransitionDefinition>,
        private val filer: Filer
) {



    private val navigationContextType = ParameterizedTypeName.get(navigationContextTypeName, baseActionsType, dependencySourceType)
    private val navigationContextParameter = ParameterSpec.builder(navigationContextType, "context").addAnnotation(NonNull::class.java).build()

    fun generateAll() {
        val rootViewType = TypeVariableName.get("VR", viewTypeName)

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

        filer.createSourceFile(baseTypeName.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(baseTypeName.packageName(), baseClassTypeSpec)
                            .build()
                            .writeTo(fileWriter)
                }

        // EMPTY STATE TRANSITIONS GENERATION

        val emptyViewTypeParameter = TypeVariableName.get("V", viewTypeName)
        val emptyWrapperTypeSpec = TypeSpec.classBuilder(emptyWrapperTypeName)
                .superclass(ParameterizedTypeName.get(baseTypeName, emptyViewTypeParameter,
                        viewSetTypeName, ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType), ParameterizedTypeName.get(baseStatesType, emptyViewTypeParameter, ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType)),
                        viewSetTypeName, ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType), ParameterizedTypeName.get(baseStatesType, emptyViewTypeParameter, ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType))
                ))
                .addTypeVariable(emptyViewTypeParameter)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(baseStatesType, genericWildcard, genericWildcard), "from").addAnnotation(NonNull::class.java).build())
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(baseStatesType, genericWildcard, genericWildcard), "to").addAnnotation(NonNull::class.java).build())
                        .addStatement("super((\$1T) from, (\$1T) to)", ParameterizedTypeName.get(baseStatesType, emptyViewTypeParameter, ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType)))
                        .build()
                )
                .addMethod(MethodSpec.methodBuilder(initWrappedMethodName)
                        .returns(ParameterizedTypeName.get(stateTransitionTypeName, baseActionsType, viewSetTypeName, viewSetTypeName, emptyViewTypeParameter, dependencySourceType))
                        .addAnnotation(Override::class.java)
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType), "from").addAnnotation(NonNull::class.java).build())
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType), "to").addAnnotation(NonNull::class.java).build())
                        .addStatement("return new \$T<>(from, to)", emptyTransitionTypeName)
                        .build())
                .build()

        filer.createSourceFile(emptyWrapperTypeName.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(emptyWrapperTypeName.packageName(), emptyWrapperTypeSpec)
                            .build()
                            .writeTo(fileWriter)
                }


        // DEFAULT STATE TRANSITIONS GENERATION

        val defaultViewTypeParameter = TypeVariableName.get("V", viewTypeName)
        val defaultWrapperTypeSpec = TypeSpec.classBuilder(defaultWrapperTypeName)
                .superclass(ParameterizedTypeName.get(baseTypeName, defaultViewTypeParameter,
                        viewSetTypeName, ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType), ParameterizedTypeName.get(baseStatesType, emptyViewTypeParameter, ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType)),
                        viewSetTypeName, ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType), ParameterizedTypeName.get(baseStatesType, emptyViewTypeParameter, ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType))
                ))
                .addTypeVariable(emptyViewTypeParameter)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(baseStatesType, genericWildcard, genericWildcard), "from").addAnnotation(NonNull::class.java).build())
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(baseStatesType, genericWildcard, genericWildcard), "to").addAnnotation(NonNull::class.java).build())
                        .addStatement("super((\$1T) from, (\$1T) to)", ParameterizedTypeName.get(baseStatesType, emptyViewTypeParameter, ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType)))
                        .build()
                )
                .addMethod(MethodSpec.methodBuilder(initWrappedMethodName)
                        .returns(ParameterizedTypeName.get(stateTransitionTypeName, baseActionsType, viewSetTypeName, viewSetTypeName, emptyViewTypeParameter, dependencySourceType))
                        .addAnnotation(Override::class.java)
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType), "from").addAnnotation(NonNull::class.java).build())
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(mStateTypeName, baseActionsType, viewSetTypeName, emptyViewTypeParameter, dependencySourceType), "to").addAnnotation(NonNull::class.java).build())
                        .addStatement("return new \$T<>(from, to)", defaultTransitionTypeName)
                        .build())
                .build()

        filer.createSourceFile(defaultWrapperTypeName.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(defaultWrapperTypeName.packageName(), defaultWrapperTypeSpec)
                            .build()
                            .writeTo(fileWriter)
                }


        // ALL TRANSITIONS GENERATIONS

        transitions.forEach { transitionDesc ->
            if (emptyTransitionTypeName == transitionDesc.typeName) return@forEach
            if (defaultTransitionTypeName == transitionDesc.typeName) return@forEach

            val stateTransitionRootViewType = viewGroupTypeName

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

            filer.createSourceFile(transitionWrapperTypeName.canonicalName())
                    .openWriter()
                    .use { fileWriter ->
                        JavaFile.builder(transitionWrapperTypeName.packageName(), transitionWrapperTypeSpecBuilder.build())
                                .build()
                                .writeTo(fileWriter)
                    }
        }

        // TRANSITIONS FACTORY

        val mStateType = ParameterizedTypeName.get(baseStatesType, genericWildcard, genericWildcard)
        val transitionsFactoryTypeSpec = TypeSpec.classBuilder(transitionsFactoryImplTypeName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(transitionsFactoryTypeName, baseActionsType, dependencySourceType, mStateType))
                .addMethod(MethodSpec.methodBuilder("getTransition")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(NonNull::class.java)
                        .addAnnotation(Override::class.java)
                        .returns(ParameterizedTypeName.get(stateTransitionTypeName, baseActionsType, genericWildcard, genericWildcard, genericWildcard, dependencySourceType))
                        .addParameter(ParameterSpec.builder(mStateType, "from").addAnnotation(NonNull::class.java).build())
                        .addParameter(ParameterSpec.builder(mStateType, "to").addAnnotation(NonNull::class.java).build())
                        .apply {
                            CodeBlock.builder()
                                    .also { codeBlock ->
                                        transitions.groupBy { it.beginStateWrapperClass }.forEach { (betinStateWrapper, transitions) ->
                                            codeBlock.beginControlFlow("if (from instanceof \$T)", betinStateWrapper)
                                            transitions.forEach {
                                                codeBlock.beginControlFlow("if (to instanceof \$T)", it.endStateWrapperClass)
                                                if (it.typeName == emptyTransitionTypeName) {
                                                    codeBlock.addStatement("return new \$T<>((\$T) from, (\$T) to)", emptyWrapperTypeName, it.beginStateWrapperClass, it.endStateWrapperClass)
                                                } else {
                                                    codeBlock.addStatement("return new \$T((\$T) from, (\$T) to)", it.wrapperTypeName, it.beginStateWrapperClass, it.endStateWrapperClass)
                                                }
                                                codeBlock.endControlFlow()

                                            }
                                            codeBlock.endControlFlow()
                                        }
                                    }
                                    .build()
                                    .also {
                                        addCode(it)
                                    }
                        }
                        .addStatement("if (\$T.DEBUG) \$T.w(getClass().getSimpleName(), \"No movement set for \" + from.getWrapped().getClass().getSimpleName() + \" to \" + to.getWrapped().getClass().getSimpleName())", moduleBuildConfig, logTypeName)
                        .addStatement("return new \$T<>(from, to)", defaultWrapperTypeName)
                        .build())
                .build()

        filer.createSourceFile(transitionsFactoryImplTypeName.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(transitionsFactoryImplTypeName.packageName(), transitionsFactoryTypeSpec)
                            .build()
                            .writeTo(fileWriter)
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