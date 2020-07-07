package name.wildswift.mapache.generator.codegen

import androidx.annotation.NonNull
import com.squareup.javapoet.*
import name.wildswift.mapache.generator.*
import name.wildswift.mapache.generator.generatemodel.LayerDefinition
import name.wildswift.mapache.generator.generatemodel.ViewContentDefinition
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class BaseClassGenerator(
        private val smTypeName: ClassName,
        private val actionBaseType: ClassName,
        private val baseStatesType: ClassName,
        private val transitionsFactoryType: ClassName,
        private val viewContentMetaSourceType: ClassName,
        private val dependencySource: TypeName,
        private val contentMetaSourceType: ClassName,
        private val layers: List<LayerDefinition>,
        private val viewContents: List<ViewContentDefinition>,
        private val filer: Filer
) {
    fun generateAll() {
        val mStateParameterizedBaseClass = ParameterizedTypeName.get(baseStatesType, viewTypeName, genericWildcard)

        val navigationStateMachineType = ParameterizedTypeName.get(navigationStateMachineTypeName, actionBaseType, dependencySource, mStateParameterizedBaseClass)
        val layerDefinitionType = ParameterizedTypeName.get(layerDefinitionTypeName, actionBaseType, dependencySource, mStateParameterizedBaseClass)

        val navStateMachineKey = FieldSpec.builder(stringTypeName, "NAVIGATION_STATE_MACHINE").addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("\"NavigationStateMachine\"").build()

        val baseInterfaceTypeSpec = TypeSpec
                .classBuilder(smTypeName)
                .addModifiers(Modifier.PUBLIC)
                .addField(navStateMachineKey)
                /*
                  public static NavigationStateMachine<TestAppEvent, DiContext, TestAppMState<ViewGroup, ?>> newNavigationStateMachine(DiContext context) {
                    List<LayerDefinition<TestAppEvent, DiContext, TestAppMState<ViewGroup, ?>>> layers = Arrays.asList(new LayerDefinition<TestAppEvent, DiContext, TestAppMState<ViewGroup, ?>>(PrimaryStateWrapper.newInstance(), android.R.id.content));
                    return new NavigationStateMachine<>(layers, new TestAppTransitionsFactory(), new TestAppViewContentMetaSource(), context);
                  }
                */
                .addMethod(MethodSpec.methodBuilder("newNavigationStateMachine")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(navigationStateMachineType)
                        .addParameter(ParameterSpec.builder(dependencySource, "context").build())
                        .addStatement(CodeBlock.builder()
                                .add("\$T layers = \n \$T.asList(\n", ParameterizedTypeName.get(listTypeName, layerDefinitionType), arraysTypeName)
                                .indent()
                                .apply {
                                    layers.dropLast(1).forEach {
                                        add("new \$T((\$T)\$T.newInstance(), ${it.contentId}),\n", layerDefinitionType, baseStatesType, it.initialStateWrapperType, it.contentIdClass)
                                    }
                                    layers.lastOrNull()?.also {
                                        add("new \$T((\$T)\$T.newInstance(), ${it.contentId})\n", layerDefinitionType, baseStatesType, it.initialStateWrapperType, it.contentIdClass)
                                    }
                                }
                                .unindent()
                                .add(")")
                                .build())
                        .addStatement("return new \$T(layers, new \$T(), new \$T(), context)", navigationStateMachineType, transitionsFactoryType, viewContentMetaSourceType)
                        .build()
                )
                .addMethod(MethodSpec.methodBuilder("getInstance")
                        .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\"unchecked\"").build())
                        .addAnnotation(AnnotationSpec.builder(suppressLintType).addMember("value", "\"WrongConstant\"").build())
                        .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(contextTypeName, "context").build())
                        .returns(navigationStateMachineType)
                        .addStatement("return (\$T) context.getSystemService(\$N)", navigationStateMachineType, navStateMachineKey)
                        .build()
                )
                .build()



        filer.createSourceFile(smTypeName.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(smTypeName.packageName(), baseInterfaceTypeSpec)
                            .build()
                            .writeTo(fileWriter)
                }

        val contentMetaSourceTypeSpec = TypeSpec
                .classBuilder(contentMetaSourceType)
                .addSuperinterface(ParameterizedTypeName.get(viewContentMetaSourceTypeName, mStateParameterizedBaseClass))
                /*
                    private final Map<Class, Set<ViewContentMeta>> mapping = new HashMap<>();
                 */
                .addField(FieldSpec
                        .builder(ParameterizedTypeName.get(mapTypeName, classTypeName, ParameterizedTypeName.get(setTypeName, viewContentMetaTypeName)), "mapping", Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("new \$T<>()", hashMapTypeName)
                        .build()
                )
                /*
                  {
                    List<ViewContentMeta> viewContentMetas;
                    viewContentMetas = Arrays.asList(new ViewContentMeta());
                    mapping.put(Class.class, Collections.unmodifiableSet(new HashSet<>(viewContentMetas)));
                  }
                 */
                .addInitializerBlock(CodeBlock.builder()
                        .addStatement("\$T<\$T> viewContentMetas", setTypeName, viewContentMetaTypeName)
                        .also { builder ->
                            viewContents
                                    .groupBy { it.targetState }
                                    .entries
                                    .forEach { (targetState, viewContents) ->
                                        builder.addStatement("viewContentMetas = new \$T<>()", hashSetTypeName)
                                        viewContents.forEach {
                                            builder.addStatement("viewContentMetas.add(new \$T(\$T.class, \$T.class, \$S, ${it.default}))", viewContentMetaTypeName, it.viewType, it.typeName, it.name)
                                        }
                                        builder.addStatement("mapping.put(\$T.class, \$T.unmodifiableSet(viewContentMetas))", targetState, collectionsTypeName)
                                    }
                        }
                        .build()
                )
                /*
                    @NonNull
                    @Override
                    public Set<ViewContentMeta> getObjectsForState(@NonNull Class<TestAppMState<ViewGroup, ?>> stateClass) {
                        Set<ViewContentMeta> result = mapping.get(stateClass);
                        if (result != null) {
                          return result;
                        }
                        return Collections.EMPTY_SET;
                    }
                */
                .addMethod(MethodSpec.methodBuilder("getObjectsForState")
                        .addAnnotation(NonNull::class.java)
                        .addAnnotation(Override::class.java)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(setTypeName, viewContentMetaTypeName))
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(classTypeName, mStateParameterizedBaseClass), "stateClass").addAnnotation(NonNull::class.java).build())
                        .addStatement("\$T result = mapping.get(stateClass)", ParameterizedTypeName.get(setTypeName, viewContentMetaTypeName))
                        .beginControlFlow("if (result != null)")
                        .addStatement("return result")
                        .endControlFlow()
                        .addStatement("return \$T.EMPTY_SET", collectionsTypeName)
                        .build()
                )
                .build()



        filer.createSourceFile(contentMetaSourceType.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(contentMetaSourceType.packageName(), contentMetaSourceTypeSpec)
                            .build()
                            .writeTo(fileWriter)
                }
    }
}