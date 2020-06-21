package name.wildswift.mapache.generator.parsers.groovy.dsldelegates

import name.wildswift.mapache.generator.parsers.groovy.model.StateMachineLayer
import java.lang.IllegalArgumentException

class LayerDelegate : GraphBaseDelegate() {

    override fun buildStateGraph() : StateMachineLayer {
        return StateMachineLayer(
                "",
                initialRaw?.first ?: throw IllegalArgumentException("No root for layer"),
                hasBackStack
        )
    }

    override fun name() = "Root Layer"
}