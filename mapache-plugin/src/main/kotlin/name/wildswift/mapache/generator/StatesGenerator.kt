package name.wildswift.mapache.generator

import org.w3c.dom.Node
import java.io.File

class StatesGenerator(
        private val nsPrefix: String?,
        private val prefix: String,
        private val statesPackageName: String,
        private val actionsPackageName: String,
        private val output: File
) {
    fun generateAll(actionsNode: Node, statesNode: Node) {
        val find = statesNode.childNodes.find(nsPrefix, "state")
    }
}