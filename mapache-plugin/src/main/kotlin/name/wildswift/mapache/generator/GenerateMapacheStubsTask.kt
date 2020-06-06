package name.wildswift.mapache.generator

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Document
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory

open class GenerateMapacheStubsTask : DefaultTask() {
    companion object {
        private const val DEBUG = true
        private const val XML_NAMESPACE_URI = "http://plugins.wild-swift.name/mapache"

        private val packageRegexp = "([a-zA-Z_][0-9a-zA-Z_]*\\.)*[a-zA-Z_][0-9a-zA-Z_]*".toRegex()
        private val relativePackageRegexp = "\\.?([a-zA-Z_][0-9a-zA-Z_]*\\.)*[a-zA-Z_][0-9a-zA-Z_]*".toRegex()
        private val appNameRegexp = "[A-Z][0-9a-zA-Z]*".toRegex()
    }

    @TaskAction
    fun generateStubs() {
        println(Class.forName("org.jetbrains.kotlin.compilerRunner.GradleCompilerRunner").methods.map { it.name }.filter { !it.startsWith("access\$") })

        val outputFile = outputs.files.files.firstOrNull() ?: return
        val inputFile = inputs.files.files.filter { it.exists() }.firstOrNull() ?: return
        if (DEBUG) {
            println(inputFile)
        }

        val xmlContents = DocumentBuilderFactory
                .newInstance()
                .apply {
                    isNamespaceAware = true
                }
                .newDocumentBuilder()
                .parse(inputFile)

        val nsPrefix = resolveNamespace(xmlContents)

        if (DEBUG) {
            println("Namespace prefix: $nsPrefix")
        }

        val rootTag = xmlContents.documentElement.takeIf { nsPrefix == it.prefix } ?: throw IllegalArgumentException("Mapache.xml file not valid")
        val actionsTag = rootTag.childNodes.find(prefix = nsPrefix, name = "actions").singleOrNull() ?: throw IllegalArgumentException("Mapache.xml file not valid")
        val statesTag = rootTag.childNodes.find(prefix = nsPrefix, name = "state-list").singleOrNull() ?: throw IllegalArgumentException("Mapache.xml file not valid")

        val appName = rootTag.attributes.find(prefix = nsPrefix, name = "appName").firstOrNull()?.nodeValue?.takeIf { appNameRegexp.matches(it) } ?: throw IllegalArgumentException("Mapache.xml file not valid")
        val basePackage = rootTag.attributes.find(prefix = nsPrefix, name = "package").firstOrNull()?.nodeValue?.takeIf { packageRegexp.matches(it) } ?: throw IllegalArgumentException("Mapache.xml file not valid")
        val actionsPackage = actionsTag.attributes
                .find(prefix = nsPrefix, name = "package")
                .firstOrNull()
                ?.nodeValue
                .let {
                    if (it != null && !relativePackageRegexp.matches(it)) throw IllegalArgumentException("Mapache.xml file not valid")
                    when {
                        it == null -> basePackage
                        it.startsWith(".") -> basePackage + it
                        else -> it
                    }
                }
        val statesPackage = statesTag.attributes
                .find(prefix = nsPrefix, name = "statesPackage")
                .firstOrNull()
                ?.nodeValue
                .let {
                    if (it != null && !relativePackageRegexp.matches(it)) throw IllegalArgumentException("Mapache.xml file not valid")
                    when {
                        it == null -> basePackage
                        it.startsWith(".") -> basePackage + it
                        else -> it
                    }
                }

        if (DEBUG) {
            println("root tag appName = $appName, basePackage = $basePackage, actionsPackage = $actionsPackage, statesPackage = $statesPackage")
        }

//        DomToInternalModelConverter.convertDomToModel(rootTag, nsPrefix)
//        ActionsGenerator(nsPrefix, appName, actionsPackage, outputFile).generateAll(actionsTag)
//        StatesGenerator(nsPrefix, appName, statesPackage, actionsPackage, outputFile).generateAll(actionsTag, statesTag)

    }


    private fun resolveNamespace(xmlContents: Document): String? {
        val namespacePrefixNode = xmlContents
                .documentElement
                .attributes
                .let { it.find(prefix = "xmlns") + it.find(name = "xmlns") }
                .filter { it.nodeValue == XML_NAMESPACE_URI }
                .firstOrNull() ?: throw IllegalArgumentException("Mapache.xml file not valid")

        return if (namespacePrefixNode.prefix != null) namespacePrefixNode.localName else null
    }
}