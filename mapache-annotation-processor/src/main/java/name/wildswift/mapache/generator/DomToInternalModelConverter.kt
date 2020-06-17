package name.wildswift.mapache.generator

import name.wildswift.mapache.generator.generatemodel.*
import name.wildswift.mapache.generator.generatemodel.Action
import name.wildswift.mapache.generator.generatemodel.Parameter
import name.wildswift.mapache.generator.generatemodel.State
import name.wildswift.mapache.generator.generatemodel.StateMachine
import name.wildswift.mapache.generator.generatemodel.StateMachineLayer
import org.w3c.dom.Node

object DomToInternalModelConverter {
    private const val DEBUG = true

    private val packageRegexp = "([a-zA-Z_][0-9a-zA-Z_]*\\.)*[a-zA-Z_][0-9a-zA-Z_]*".toRegex()
    private val relativePackageRegexp = "\\.?([a-zA-Z_][0-9a-zA-Z_]*\\.)*[a-zA-Z_][0-9a-zA-Z_]*".toRegex()
    private val elementNameRegexp = "[A-Z][0-9a-zA-Z]*".toRegex()
    private val typeNameRegexp = "(([a-zA-Z_][0-9a-zA-Z_]*\\.)*[A-Z][0-9a-zA-Z_]*)|string|int|bool|char|byte|long|float|double|short".toRegex()
    private val classNameRegexp = "([a-zA-Z_][0-9a-zA-Z_]*\\.)*[A-Z][0-9a-zA-Z_]*".toRegex()
    private val fieldNameRegexp = "[a-zA-Z_][0-9a-zA-Z_]*".toRegex()
    private val appNameRegexp = "[A-Z][0-9a-zA-Z]*".toRegex()
    private val resIdRegexp = "[a-z_][0-9a-z_]*".toRegex()

    fun convertDomToModel(rootTag: Node, nsPrefix: String?): StateMachine {

        val actionsTag = rootTag.childNodes.find(prefix = nsPrefix, name = "actions").singleOrNull() ?: throw IllegalArgumentException("Mapache.xml file not valid")
        val statesTag = rootTag.childNodes.find(prefix = nsPrefix, name = "state-list").singleOrNull() ?: throw IllegalArgumentException("Mapache.xml file not valid")
        val layersTag = rootTag.childNodes.find(prefix = nsPrefix, name = "layers").singleOrNull() ?: throw IllegalArgumentException("Mapache.xml file not valid")

        val backAction = Action(
                name = actionsTag.attributes.find(prefix = nsPrefix, name = "backAction").singleOrNull()?.nodeValue
                        ?: throw IllegalArgumentException("Mapache.xml file not valid"),
                params = listOf()
        )
        val actions = actionsTag
                .childNodes
                .find(prefix = nsPrefix, name = "action")
                .map {
                    Action(
                            name = it.attributes.find(prefix = nsPrefix, name = "name").singleOrNull()?.nodeValue
                                    ?: throw IllegalArgumentException("Mapache.xml file not valid"),
                            params = it.childNodes.find(prefix = nsPrefix, name = "arg").map {
                                Parameter(
                                        name = it.attributes.find(prefix = nsPrefix, name = "name").singleOrNull()?.nodeValue
                                                ?: throw IllegalArgumentException("Mapache.xml file not valid"),
                                        type = it.attributes.find(prefix = nsPrefix, name = "type").singleOrNull()?.nodeValue
                                                ?: throw IllegalArgumentException("Mapache.xml file not valid")
                                )
                            }
                    )
                }
                .let {
                    it + backAction
                }


        // check for unique
        if (actions.map { it.name }.size != actions.map { it.name }.toSet().size) throw IllegalArgumentException("Mapache.xml file not valid")


        val states = ArrayList<State>()

        val machine = layersTag
                .childNodes
                .find(prefix = nsPrefix, name = "layer")
                .map {
                    val contentIdResName = it.attributes.find(prefix = nsPrefix, name = "content-id").singleOrNull()?.nodeValue
                            ?: throw IllegalArgumentException("Mapache.xml file not valid")
                    val initialStateName = it.attributes.find(prefix = nsPrefix, name = "start-from").singleOrNull()?.nodeValue
                            ?: throw IllegalArgumentException("Mapache.xml file not valid")

                    StateMachineLayer(
                            contentIdResName,
                            getState(initialStateName, states, statesTag, nsPrefix, actions),
                            true
                    )
                }
                .let {
                    StateMachine(it, listOf(), "", "", "", "", Object::class.java.name)
                }

        if (DEBUG) {
            println(states.map { it.name })
        }

        return machine
    }

    fun getState(name: String, states: ArrayList<State>, statesTag: Node, nsPrefix: String?, actions: List<Action>): State {
        val result = states.firstOrNull { it.name == name }
        if (result != null) return result

        val stateTag = statesTag
                .childNodes
                .find(prefix = nsPrefix, name = "state")
                .first {
                        it.attributes.find(prefix = nsPrefix, name = "name").singleOrNull()?.nodeValue == name
                }
        val stateObj = State(
                name = name,
                parameters = listOf(),
                child = null
        )
        states.add(stateObj)
        stateObj.movements = stateTag
                .childNodes
                .find(prefix = nsPrefix, name = "do-on")
                .map {
                    val actionName = it.attributes.find(prefix = nsPrefix, name = "action").singleOrNull()?.nodeValue
                            ?: throw IllegalArgumentException("Mapache.xml file not valid")
                    val targetState = it.attributes.find(prefix = nsPrefix, name = "go-to").singleOrNull()?.nodeValue
                            ?: throw IllegalArgumentException("Mapache.xml file not valid")
                    val transition = it.attributes.find(prefix = nsPrefix, name = "go-to").singleOrNull()?.nodeValue
                            ?: TODO("Implement as empty transition")

                    Movement(
                            action = actions.first { it.name == actionName },
                            endState = getState(targetState, states, statesTag, nsPrefix, actions),
                            implClass = transition
                    )
                }
        return stateObj
    }
}