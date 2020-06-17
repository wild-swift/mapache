package name.wildswift.mapache.generator.codegen

import androidx.annotation.NonNull
import com.squareup.javapoet.*
import name.wildswift.mapache.generator.*
import name.wildswift.mapache.generator.codegen.GenerationConstants.getWrappedMethodName
import name.wildswift.mapache.generator.codegen.GenerationConstants.initWrappedMethodName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier


class TransitionsWrapperGenerator(
        private val prefix: String,
        private val packageName: String,
        private val processingEnv: ProcessingEnvironment,
        private val baseActionsType: ClassName,
        private val baseStatesType: ClassName,
        private val dependencySourceType: TypeName

) {

    val baseTypeName = ClassName.get(packageName, "${prefix}StateTransition")

    private val navigationContextType = ParameterizedTypeName.get(navigationContextTypeName, baseActionsType, dependencySourceType)
    private val navigationContextParameter = ParameterSpec.builder(navigationContextType, "context").addAnnotation(NonNull::class.java).build()

    fun generateAll() {
        val inViewSetType = TypeVariableName.get("VS_IN", viewSetTypeName)
        val inStateType = TypeVariableName.get("S_IN", ParameterizedTypeName.get(mStateTypeName, baseActionsType, inViewSetType, dependencySourceType))
        val inStateWrapperType = TypeVariableName.get("SS", ParameterizedTypeName.get(baseStatesType, inStateType))

        val outViewSetType = TypeVariableName.get("VS_OUT", viewSetTypeName)
        val outStateType = TypeVariableName.get("S_OUT", ParameterizedTypeName.get(mStateTypeName, baseActionsType, outViewSetType, dependencySourceType))
        val outStateWrapperType = TypeVariableName.get("TS", ParameterizedTypeName.get(baseStatesType, outStateType))

        val wrappedObjectType = ParameterizedTypeName.get(stateTransitionTypeName, baseActionsType, inViewSetType, outViewSetType, dependencySourceType)
        val wrappedField = FieldSpec.builder(wrappedObjectType, "wrappedObj", Modifier.PRIVATE, Modifier.FINAL)
                .build()
        val internalCallbakWrapper = buildCallbackWrapperType(outViewSetType)

        val baseClassTypeSpec = TypeSpec
                .classBuilder(baseTypeName)
                .addTypeVariables(listOf(inViewSetType, inStateType, inStateWrapperType))
                .addTypeVariables(listOf(outViewSetType, outStateType, outStateWrapperType))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(ParameterizedTypeName.get(stateTransitionTypeName, baseActionsType, viewSetTypeName, viewSetTypeName, dependencySourceType))
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
                        .addParameter(ParameterSpec.builder(viewGroupClass, "rootView").addAnnotation(NonNull::class.java).build())
                        .addParameter(ParameterSpec.builder(viewSetTypeName, "inViews").addAnnotation(NonNull::class.java).build())
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(stateTransitionCallbackTypeName, viewSetTypeName), "callback").addAnnotation(NonNull::class.java).build())
                        .addStatement("\$N.execute(\$N, rootView, (\$T) inViews, new \$N(callback))", wrappedField, navigationContextParameter, inViewSetType, internalCallbakWrapper)
                        .build())
                .addType(internalCallbakWrapper)
                .build()

/*
    public void execute(@NonNull NavigationContext<AlarmClockEvent, ServicesRepository> context, @Nullable FrameLayout rootView, @Nullable ViewSet inViews, @NonNull final TransitionCallback<ViewSet> callback) {
        wrapped.execute(context, rootView, (VS_IN) inViews, new TransitionCallbackWrapper(callback));
    }
 */
        processingEnv.filer.createSourceFile(baseTypeName.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(packageName, baseClassTypeSpec)
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