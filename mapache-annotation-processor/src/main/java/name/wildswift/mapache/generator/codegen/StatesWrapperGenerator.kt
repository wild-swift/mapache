package name.wildswift.mapache.generator.codegen

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.squareup.javapoet.*
import name.wildswift.mapache.generator.*
import name.wildswift.mapache.generator.codegen.GenerationConstants.createInstanceMethodName
import name.wildswift.mapache.generator.codegen.GenerationConstants.getWrappedMethodName
import name.wildswift.mapache.generator.generatemodel.Action
import name.wildswift.mapache.generator.generatemodel.State
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier


class StatesWrapperGenerator(
        private val prefix: String,
        private val packageName: String,
        private val processingEnv: ProcessingEnvironment,
        modulePackageName: String,
        private val dependencySource: TypeName,
        private val actionBaseType: ClassName,
        private val actionTypes: Map<Action, ClassName>,
        private val states: List<State>
) {

    val baseTypeName = ClassName.get(packageName, "${prefix}MState")
    val statesNames = states.map { state -> state to ClassName.get(packageName, "${state.name.split(".").last()}Wrapper") }.toMap()


    private val moduleBuildConfig = ClassName.get(modulePackageName, "BuildConfig")
    private val navigationContextType = ParameterizedTypeName.get(navigationContextTypeName, actionBaseType, dependencySource)
    private val navigationContextParameter = ParameterSpec.builder(navigationContextType, "context").addAnnotation(NonNull::class.java).build()

    @SuppressWarnings("DefaultLocale")
    fun generateAll() {
        val wrappedTypeVarible = TypeVariableName.get("MS", ParameterizedTypeName.get(mStateTypeName, actionBaseType, genericWildcard, dependencySource))
        val getWrappedMethod = MethodSpec.methodBuilder(getWrappedMethodName)
                .addAnnotation(NonNull::class.java)
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

            val typeElement = processingEnv.elementUtils.getTypeElement(state.name)
            val thisStateViewSetType = typeElement.interfaces
                    ?.mapNotNull { TypeName.get(it) as? ParameterizedTypeName }
                    ?.firstOrNull { (it as? ParameterizedTypeName)?.rawType == mStateTypeName }
                    .let {
                        it ?: error("Class ${state.name} not implements ${mStateTypeName.canonicalName()}")
                    }
                    .typeArguments
                    .apply { check(size == 3) }
                    .get(1)


            val currentStateWrapperName = statesNames[state] ?: error("Internal error")
            val currentStateName = ClassName.bestGuess(state.name)
            val wrappedField = FieldSpec.builder(currentStateName, "wrappedObj").addModifiers(Modifier.PRIVATE, Modifier.FINAL).build()
            val parameterList = state.parameters.orEmpty()

            val stateWrapperTypeSpecBuilder = TypeSpec
                    .classBuilder(currentStateWrapperName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .superclass(ParameterizedTypeName.get(baseTypeName, currentStateName))
                    .addField(wrappedField)
                    /*
                      @Override
                      @NonNull
                      public BuyStep1State getWrapped() {
                        return wrappedObj;
                      }
                     */
                    .addMethod(MethodSpec.methodBuilder(getWrappedMethodName)
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override::class.java)
                            .addAnnotation(NonNull::class.java)
                            .returns(currentStateName)
                            .addStatement("return \$N", wrappedField)
                            .build()
                    )
                    /*
                      @Override
                      @Nullable
                      public TestAppMState getNextState(@NonNull TestAppEvent e) {
                        if (e instanceof ProceedBuy) return ReviewBuyStateWrapper.newInstance(((ProceedBuy)e).getTiker(), ((ProceedBuy)e).getAmount(), ((ProceedBuy)e).getPaymentType());
                        if (BuildConfig.DEBUG) throw new IllegalStateException("Unable to process event " + e.getClass().getSimpleName());
                        return null;
                      }
                     */
                    .addMethod(MethodSpec.methodBuilder("getNextState")
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override::class.java)
                            .addAnnotation(Nullable::class.java)
                            .addParameter(ParameterSpec.builder(actionBaseType, "e").addAnnotation(NonNull::class.java).build())
                            .returns(baseTypeName)
                            .apply {
                                state.movements.forEach { (action, endState, _) ->
                                    val parametersSting = endState.parameters.orEmpty().joinToString { "((\$1T)e).get${it.name.capitalize()}()" }
                                    addStatement("if (e instanceof \$1T) return \$2T.${createInstanceMethodName}($parametersSting)", actionTypes[action], statesNames[endState])
                                }
                                addStatement("if (\$1T.DEBUG) throw new \$2T(\"Unable to process event \" + e.getClass().getSimpleName())", moduleBuildConfig, ClassName.get(IllegalStateException::class.java))
                                addStatement("return null")
                            }
                            .build()
                    )
                    /*
                      @Override
                      @NonNull
                      public ViewCouple<RootView, BuyCurrencyStep1View> setup(@NonNull ViewGroup rootView, @NonNull NavigationContext<TestAppEvent, DiContext> context) {
                        return wrappedObj.setup(rootView, context);
                      }
                     */
                    .addMethod(MethodSpec.methodBuilder("setup")
                            .addAnnotation(Override::class.java)
                            .addAnnotation(NonNull::class.java)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ParameterSpec.builder(viewGroupClass, "rootView").addAnnotation(NonNull::class.java).build())
                            .addParameter(navigationContextParameter)
                            .returns(thisStateViewSetType)
                            .addStatement("return \$N.setup(rootView, context)", wrappedField)
                            .build()
                    )
                    /*
                      @Override
                      @NonNull
                      @SuppressWarnings("unchecked")
                      public Runnable dataBind(@NonNull NavigationContext<TestAppEvent, DiContext> context, @NonNull ViewSet views) {
                        return wrappedObj.dataBind(context, (ViewCouple<RootView, BuyCurrencyStep1View>) views);
                      }
                     */
                    .addMethod(MethodSpec.methodBuilder("dataBind")
                            .addAnnotation(Override::class.java)
                            .addAnnotation(NonNull::class.java)
                            .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\"unchecked\"").build())
                            .addModifiers(Modifier.PUBLIC)
                            .returns(runnableTypeName)
                            .addParameter(navigationContextParameter)
                            .addParameter(ParameterSpec.builder(viewSetTypeName, "views").addAnnotation(NonNull::class.java).build())
                            .addStatement("return \$N.dataBind(context, (\$T) views)", wrappedField, thisStateViewSetType)
                            .build()
                    )
                    /*
                      @Override
                      @NonNull
                      public Runnable start(@NonNull NavigationContext<TestAppEvent, DiContext> context) {
                        return wrappedObj.start(context);
                      }
                     */
                    .addMethod(MethodSpec.methodBuilder("start")
                            .addAnnotation(Override::class.java)
                            .addAnnotation(NonNull::class.java)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(runnableTypeName)
                            .addParameter(navigationContextParameter)
                            .addStatement("return \$N.start(context)", wrappedField)
                            .build()
                    )

            if (parameterList.isEmpty()) {
                val instanceField = FieldSpec.builder(currentStateWrapperName, "instance", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("new \$T()", currentStateWrapperName).build()
                stateWrapperTypeSpecBuilder
                        .addField(instanceField)
                        .addMethod(MethodSpec.methodBuilder(createInstanceMethodName)
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .returns(currentStateWrapperName)
                                .addStatement("return \$N", instanceField)
                                .build())
                        .addMethod(MethodSpec
                                .constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .addStatement("\$N = new \$T()", wrappedField, currentStateName)
                                .build()
                        )
            } else {
                stateWrapperTypeSpecBuilder
                        .addMethod(MethodSpec.methodBuilder(createInstanceMethodName)
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .addParameters(
                                        parameterList.map {
                                            ParameterSpec.builder(it.type.toType(), it.name).build()
                                        }
                                )
                                .returns(currentStateWrapperName)
                                .addStatement("return new \$T(${parameterList.joinToString { it.name }})", currentStateWrapperName)
                                .build())
                        .addMethod(MethodSpec
                                .constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .addParameters(
                                        parameterList.map {
                                            ParameterSpec.builder(it.type.toType(), it.name).build()
                                        }
                                )
                                .addStatement("\$N = new \$T(${parameterList.joinToString { it.name }})", wrappedField, currentStateName)
                                .build()
                        )
            }

            processingEnv.filer.createSourceFile(currentStateWrapperName.canonicalName())
                    .openWriter()
                    .use { fileWriter ->
                        JavaFile.builder(packageName, stateWrapperTypeSpecBuilder.build())
                                .build()
                                .writeTo(fileWriter)
                    }
        }
    }

}