package name.wildswift.mapache.generator

import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes("name.wildswift.mapache.GenerateNavigation")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
class StatesGenerator: AbstractProcessor() {
    val generationPath: File by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        File(processingEnv.options["kapt.kotlin.generated"] ?: ".")
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
//        processingEnv.filer.createSourceFile()
        return true
    }
}