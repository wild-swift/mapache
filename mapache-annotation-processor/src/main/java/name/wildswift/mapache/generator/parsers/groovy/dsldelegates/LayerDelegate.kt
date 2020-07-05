package name.wildswift.mapache.generator.parsers.groovy.dsldelegates

import name.wildswift.mapache.generator.parsers.groovy.model.StateMachineLayer

class LayerDelegate : GraphBaseDelegate() {

    var contentId: Int = 0

    fun contentId(value: Int) {
        contentId = value
    }

    override fun buildStateGraph() : StateMachineLayer {
        return StateMachineLayer(
                contentId,
                initialRaw?.first ?: throw IllegalArgumentException("No root for layer"),
                hasBackStack
        )
    }

    override fun name() = "Root Layer"
}