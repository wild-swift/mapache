package name.wildswift.mapache.generator

import name.wildswift.mapache.generator.parsers.groovy.model.Action
import name.wildswift.mapache.generator.parsers.groovy.model.StateMachine
import name.wildswift.mapache.generator.parsers.groovy.model.StateMachineLayer

object InternalModelUpdater {

    fun verifyAndUpdateInternalModel(stateMachine: StateMachine) {

        stateMachine.layers.forEachIndexed { index, layer ->
            verifyAndUpdateLayer(layer, null)
        }

    }

    private fun verifyAndUpdateLayer(stateMachine: StateMachineLayer, backedAction: Action?) {

    }
}