package name.wildswift.mapache.generator.codegen

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.squareup.javapoet.*
import name.wildswift.mapache.generator.*
import name.wildswift.mapache.generator.codegen.GenerationConstants.createInstanceMethodName
import name.wildswift.mapache.generator.codegen.GenerationConstants.getWrappedMethodName
import name.wildswift.mapache.generator.generatemodel.StateDefinition
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier


class StatesWrapperGenerator(
        private val baseTypeName: ClassName,
        private val actionBaseType: ClassName,
        private val dependencySource: TypeName,
        private val moduleBuildConfig: ClassName,
        private val states: List<StateDefinition>,
        private val processingEnv: ProcessingEnvironment
) {

    private val navigationContextType = ParameterizedTypeName.get(navigationContextTypeName, actionBaseType, dependencySource)
    private val navigationContextParameter = ParameterSpec.builder(navigationContextType, "context").addAnnotation(NonNull::class.java).build()

    @SuppressWarnings("DefaultLocale")
    fun generateAll() {
        val rootTypeVariable = TypeVariableName.get("VR", viewTypeName)
        val wrappedTypeVarible = TypeVariableName.get("MS", ParameterizedTypeName.get(mStateTypeName, actionBaseType, genericWildcard, rootTypeVariable, dependencySource))
        val getWrappedMethod = MethodSpec.methodBuilder(getWrappedMethodName)
                .addAnnotation(NonNull::class.java)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(wrappedTypeVarible)
                .build()

        val baseClassTypeSpec = TypeSpec
                .classBuilder(baseTypeName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addTypeVariable(rootTypeVariable)
                .addTypeVariable(wrappedTypeVarible)
                .addSuperinterface(ParameterizedTypeName.get(mStateTypeName, actionBaseType, viewSetTypeName, rootTypeVariable, dependencySource))
                .addSuperinterface(ParameterizedTypeName.get(navigatableTypeName, actionBaseType, dependencySource, ParameterizedTypeName.get(baseTypeName, rootTypeVariable, genericWildcard)))
                .addMethod(getWrappedMethod)
                .build()

        processingEnv.filer.createSourceFile(baseTypeName.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(baseTypeName.packageName(), baseClassTypeSpec)
                            .build()
                            .writeTo(fileWriter)
                }

        states.forEach { state ->

            val stateRootViewType = viewGroupTypeName

            val thisStateViewSetType = state.viewSetClassName

            val currentStateWrapperName = state.wrapperClassName
            val currentStateName = state.stateClassName

            val wrappedField = FieldSpec.builder(currentStateName, "wrappedObj").addModifiers(Modifier.PRIVATE, Modifier.FINAL).build()
            val parameterList = state.parameters

            val stateWrapperTypeSpecBuilder = TypeSpec
                    .classBuilder(currentStateWrapperName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .superclass(ParameterizedTypeName.get(baseTypeName, stateRootViewType, currentStateName))
                    .addField(wrappedField)
                    /*
                        Add constructor fields
                     */
                    .also { builder ->
                        parameterList.forEach { parameter ->
                            builder.addField(FieldSpec.builder(parameter.type, parameter.name).addModifiers(Modifier.PRIVATE, Modifier.FINAL).build())
                        }
                    }
                    .addMethod(MethodSpec
                            .constructorBuilder()
                            .addModifiers(Modifier.PRIVATE)
                            .addParameters(
                                    parameterList.map {
                                        ParameterSpec.builder(it.type, it.name).build()
                                    }
                            )
                            .also { builder ->
                                parameterList.forEach { parameter ->
                                    builder.addStatement("this.${parameter.name} = ${parameter.name}")
                                }
                            }
                            .addStatement("\$N = new \$T(${parameterList.joinToString { it.name }})", wrappedField, currentStateName)
                            .build()
                    )
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
                                state.moveDefenition.forEach { moveDefenition ->
                                    val parametersSting = moveDefenition.moveParameters.joinToString { "((\$1T)e).get${it.name.capitalize()}()" }
                                    addStatement("if (e instanceof \$1T) return \$2T.${createInstanceMethodName}($parametersSting)", moveDefenition.actionType, moveDefenition.targetStateWrapperClass)
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
                            .addParameter(ParameterSpec.builder(viewGroupTypeName, "rootView").addAnnotation(NonNull::class.java).build())
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
                    /*
                      @Nullable
                      @Override
                      public BackStackEntry<BuyStep1StateWrapper> getBackStackEntry() {
                        return new BackStackEntry<>(BuyStep1StateWrapper.class, new Object[]{ tiker });
                      }
                     */
                    .addMethod(MethodSpec.methodBuilder("getBackStackEntry")
                            .addAnnotation(Nullable::class.java)
                            .addAnnotation(Override::class.java)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(ParameterizedTypeName.get(backStackEntryTypeName, currentStateWrapperName))
                            .also {
                                if(state.addToBackStack) {
                                    it.addStatement("return new \$T<>(\$T.class, new \$T[]{ ${parameterList.joinToString { "this.${it.name}" }} })", backStackEntryTypeName, currentStateWrapperName, serializableTypeName)
                                } else {
                                    it.addStatement("return null")
                                }
                            }
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
            } else {
                stateWrapperTypeSpecBuilder
                        .addMethod(MethodSpec.methodBuilder(createInstanceMethodName)
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .addParameters(
                                        parameterList.map {
                                            // use box types to simplify search this method by refltction
                                            ParameterSpec.builder(it.type.let { if (it.isPrimitive) it.box() else it }, it.name).build()
                                        }
                                )
                                .returns(currentStateWrapperName)
                                .addStatement("return new \$T(${parameterList.joinToString { it.name }})", currentStateWrapperName)
                                .build())
            }

            processingEnv.filer.createSourceFile(currentStateWrapperName.canonicalName())
                    .openWriter()
                    .use { fileWriter ->
                        JavaFile.builder(currentStateWrapperName.packageName(), stateWrapperTypeSpecBuilder.build())
                                .build()
                                .writeTo(fileWriter)
                    }
        }
    }

}