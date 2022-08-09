package name.wildswift.mapache.generator

import com.android.build.gradle.AppExtension
import com.android.build.gradle.FeatureExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency

class MapachePlugin: Plugin<Project> {

    companion object {
        private const val DEBUG = true
    }

    override fun apply(project: Project) {
        if (DEBUG) {
            println("apply")
        }
        project.dependencies.add("implementation", DefaultExternalModuleDependency("name.wildswift.android", "mapache-library", "1.0"))

        project.extensions.findByType(AppExtension::class.java)?.applicationVariants?.all {
            processVariant(project, it)
        }
        project.extensions.findByType(LibraryExtension::class.java)?.libraryVariants?.all {
            processVariant(project, it)
        }
    }

    private fun processVariant(project: Project, variant: BaseVariant) {
        if (DEBUG) {
            println("processVariant")
        }
        val pack = variant.applicationId

        variant.outputs.all { output ->
            val processResources = output.processResourcesProvider


            val outputDir = project.buildDir.resolve("generated/source/mapache/${variant.dirName}/${output.dirName}")

            if (DEBUG) {
                println("pack = $pack, project.rootDir = ${project.projectDir}, outputDir = $outputDir")
            }

//            val task = project.tasks
//                    .create("generateMapache${output.name.capitalize()}", GenerateMapacheStubsTask::class.java)
//                    .apply {
//                        val xmlFile = project.projectDir.resolve("mapache.xml")
//                        val groovyFile = project.projectDir.resolve("mapache.groovy")
//                        inputs.files(groovyFile, xmlFile)
//                        outputs.dir(outputDir)
//                        dependsOn(processResources.get())
//                    }
//            variant.registerJavaGeneratingTask(task, outputDir)
        }
    }

}