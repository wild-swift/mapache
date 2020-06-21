package name.wildswift.mapache.generator.grdsl

import groovy.lang.Closure
import name.wildswift.mapache.generator.parsers.groovy.Action
import name.wildswift.mapache.generator.parsers.groovy.StateMachine
import name.wildswift.mapache.generator.parsers.groovy.StateMachineLayer
import java.lang.IllegalArgumentException

class MapacheGroovyDslDelegate {
    private var actions = listOf<Action>()
    private var layers = listOf<StateMachineLayer>()

    val stateMachine by lazy {
        StateMachine(
                layers,
                actions,
                basePackageName
                        ?: throw IllegalArgumentException("Missing property basePackageName"),
                (if (actionsPackageName.startsWith(".")) basePackageName?.let { it + actionsPackageName } else actionsPackageName)
                        ?: throw IllegalArgumentException("Missing property basePackageName"),
                (if (statesPackageName.startsWith(".")) basePackageName?.let { it + statesPackageName } else statesPackageName)
                        ?: throw IllegalArgumentException("Missing property basePackageName"),
                (if (transitionsPackageName.startsWith(".")) basePackageName?.let { it + transitionsPackageName } else transitionsPackageName)
                        ?: throw IllegalArgumentException("Missing property basePackageName"),
                dependencySource.name
        )
    }

    private var actionsPackageName = ".events"
    var basePackageName: String? = null
    var statesPackageName = ".states"
    var transitionsPackageName = ".transitions"
    var dependencySource: Class<*> = Object::class.java

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

    fun basePackageName(value: String) {
        basePackageName = value
    }

    fun statesPackageName(value: String) {
        statesPackageName = value
    }

    fun transitionsPackageName(value: String) {
        transitionsPackageName = value
    }

    fun dependencySource(value: Class<*>) {
        dependencySource = value
    }
}

