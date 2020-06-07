package name.wildswift.mapache.generator

import name.wildswift.mapache.generator.generatemodel.Action
import name.wildswift.mapache.generator.generatemodel.StateMachine
import name.wildswift.mapache.generator.generatemodel.StateMachineLayer

object InternalModelUpdater {

    fun verifyAndUpdateInternalModel(stateMachine: StateMachine) {

        stateMachine.layers.forEachIndexed { index, layer ->
            verifyAndUpdateLayer(layer, null)
        }

    }

    private fun verifyAndUpdateLayer(stateMachine: StateMachineLayer, backedAction: Action?) {

    }
}