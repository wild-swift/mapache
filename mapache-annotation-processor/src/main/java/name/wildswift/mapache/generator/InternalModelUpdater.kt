package name.wildswift.mapache.generator

import name.wildswift.mapache.generator.parsers.groovy.Action
import name.wildswift.mapache.generator.parsers.groovy.StateMachine
import name.wildswift.mapache.generator.parsers.groovy.StateMachineLayer

object InternalModelUpdater {

    fun verifyAndUpdateInternalModel(stateMachine: StateMachine) {

        stateMachine.layers.forEachIndexed { index, layer ->
            verifyAndUpdateLayer(layer, null)
        }

    }

    private fun verifyAndUpdateLayer(stateMachine: StateMachineLayer, backedAction: Action?) {

    }
}