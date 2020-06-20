package name.wildswift.mapache.generator

import name.wildswift.mapache.generator.parsers.groovydsl.Action
import name.wildswift.mapache.generator.parsers.groovydsl.StateMachine
import name.wildswift.mapache.generator.parsers.groovydsl.StateMachineLayer

object InternalModelUpdater {

    fun verifyAndUpdateInternalModel(stateMachine: StateMachine) {

        stateMachine.layers.forEachIndexed { index, layer ->
            verifyAndUpdateLayer(layer, null)
        }

    }

    private fun verifyAndUpdateLayer(stateMachine: StateMachineLayer, backedAction: Action?) {

    }
}