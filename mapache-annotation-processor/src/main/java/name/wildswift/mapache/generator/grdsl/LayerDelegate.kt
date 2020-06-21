package name.wildswift.mapache.generator.grdsl

import name.wildswift.mapache.generator.parsers.groovy.StateMachineLayer
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