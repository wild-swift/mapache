package name.wildswift.mapache.generator.grdsl

import groovy.lang.Closure
import name.wildswift.mapache.generator.dslmodel.Action
import name.wildswift.mapache.generator.dslmodel.StateMachine
import name.wildswift.mapache.generator.dslmodel.StateMachineLayer
import java.lang.IllegalArgumentException

class MapacheGroovyDslDelegate {
    private var actions = listOf<Action>()
    private var layers = listOf<StateMachineLayer>()

    val stateMachine by lazy { StateMachine(
            layers,
            basePackageName ?: throw IllegalArgumentException("Missing property basePackageName"),
            (if (actionsPackageName.startsWith(".")) basePackageName?.let { it + actionsPackageName } else actionsPackageName) ?: throw IllegalArgumentException("Missing property basePackageName"),
            (if (statesPackageName.startsWith(".")) basePackageName?.let { it + statesPackageName } else statesPackageName) ?: throw IllegalArgumentException("Missing property basePackageName")
    ) }

    private var basePackageName: String? = null
    private var statesPackageName = ".generated"
    private var actionsPackageName = ".events"

    fun actions(invoker: Closure<Unit>) {
        val actionsDelegate = ActionsDelegate()
        invoker.delegate = actionsDelegate
        invoker.resolveStrategy = Closure.DELEGATE_FIRST
        invoker.call()
        actions = actionsDelegate.actions
        actionsPackageName = actionsDelegate.packageName
        println("$actionsPackageName: $actions")

    }

    fun layer(invoker: Closure<Unit>) {
        val layerDelegate = LayerDelegate()
        invoker.delegate = layerDelegate
        invoker.resolveStrategy = Closure.DELEGATE_FIRST
        invoker.call()
        layerDelegate.doFinal(actions)

        layers += layerDelegate.buildStateGraph()

//        println(layerDelegate.buildStateGraph())
    }

    private fun basePackageName(value: String) {
        basePackageName = value
    }

    private fun statesPackageName(value: String) {
        statesPackageName = value
    }
}

