package name.wildswift.mapache.generator.codegen

import com.squareup.javapoet.*
import name.wildswift.mapache.generator.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier


/*
    public static NavigationStateMachine<AlarmClockEvent, ViewGroup, ServicesRepository, AlarmClockMState<ViewGroup, ?>> getInstance(Context context) {
        return (NavigationStateMachine<AlarmClockEvent, ViewGroup, ServicesRepository, AlarmClockMState<ViewGroup, ?>>) context.getSystemService(NAVIGATION_STATE_MACHINE);
    }
*/
class BaseClassGenerator(
        private val smTypeName: ClassName,
        private val actionBaseType: ClassName,
        private val rootStateType: ClassName,
        private val baseStatesType: ClassName,
        private val transitionsFactoryType: ClassName,
        private val viewContentMetaSourceType: ClassName,
        private val dependencySource: TypeName,
        private val filer: Filer
) {
    fun generateAll() {
        val navigationStateMachineType = ParameterizedTypeName.get(navigationStateMachineTypeName, actionBaseType, viewGroupTypeName, dependencySource, ParameterizedTypeName.get(baseStatesType, viewGroupTypeName, genericWildcard))
        val navStateMachineKey = FieldSpec.builder(stringTypeName, "NAVIGATION_STATE_MACHINE").addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("\"NavigationStateMachine\"").build()
        val baseInterfaceTypeSpec = TypeSpec
                .classBuilder(smTypeName)
                .addModifiers(Modifier.PUBLIC)
                .addField(navStateMachineKey)
                .addMethod(MethodSpec.methodBuilder("newNavigationStateMachine")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(navigationStateMachineType)
                        .addParameter(ParameterSpec.builder(dependencySource, "context").build())
                        .addStatement("return new \$T<>(\$T.newInstance(), new \$T(), new \$T(), context)", navigationStateMachineTypeName, rootStateType, transitionsFactoryType, viewContentMetaSourceType)
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
    }
}