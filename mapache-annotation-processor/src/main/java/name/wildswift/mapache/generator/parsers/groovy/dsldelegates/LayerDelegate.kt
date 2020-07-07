package name.wildswift.mapache.generator.parsers.groovy.dsldelegates

import name.wildswift.mapache.generator.parsers.groovy.model.StateMachineLayer

class LayerDelegate : GraphBaseDelegate() {

    override fun buildStateGraph() : StateMachineLayer {
        return StateMachineLayer(
                rootView.takeIf { it > 0 } ?: 0,
                rootViewClass.takeIf { rootView > 0 },
                initialRaw?.first ?: throw IllegalArgumentException("No root for layer"),
                hasBackStack
        )
    }

    override fun name() = "Root Layer"
}