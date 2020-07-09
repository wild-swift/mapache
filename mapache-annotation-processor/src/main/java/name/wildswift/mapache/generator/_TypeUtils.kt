package name.wildswift.mapache.generator

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.WildcardTypeName
import name.wildswift.mapache.LayerDefinition
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.NavigationStateMachine
import name.wildswift.mapache.dafaults.DefaultStateTransition
import name.wildswift.mapache.dafaults.EmptyStateTransition
import name.wildswift.mapache.graph.*
import name.wildswift.mapache.states.MState
import name.wildswift.mapache.viewcontent.ViewContent
import name.wildswift.mapache.viewcontent.ViewContentMeta
import name.wildswift.mapache.viewcontent.ViewContentMetaSource
import name.wildswift.mapache.viewsets.ViewSet
import java.io.Serializable
import java.util.*
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import kotlin.collections.HashMap
import kotlin.collections.HashSet

fun String.toType(): TypeName {
    if (equals("void")) return TypeName.VOID
    if (equals("boolean")) return TypeName.BOOLEAN
    if (equals("byte")) return TypeName.BYTE
    if (equals("short")) return TypeName.SHORT
    if (equals("int")) return TypeName.INT
    if (equals("long")) return TypeName.LONG
    if (equals("char")) return TypeName.CHAR
    if (equals("float")) return TypeName.FLOAT
    if (equals("double")) return TypeName.DOUBLE

    if (equals("java.lang.Object")) return TypeName.OBJECT
    if (equals("java.lang.Void")) return TypeName.VOID.box()
    if (equals("java.lang.Boolean")) return TypeName.BOOLEAN.box()
    if (equals("java.lang.Byte")) return TypeName.BYTE.box()
    if (equals("java.lang.Short")) return TypeName.SHORT.box()
    if (equals("java.lang.Integer")) return TypeName.INT.box()
    if (equals("java.lang.Long")) return TypeName.LONG.box()
    if (equals("java.lang.Character")) return TypeName.CHAR.box()
    if (equals("java.lang.Float")) return TypeName.FLOAT.box()
    if (equals("java.lang.Double")) return TypeName.DOUBLE.box()

    return ClassName.get(split(".").dropLast(1).joinToString("."), split(".").lastOrNull())
}

val genericWildcard = WildcardTypeName.subtypeOf(Object::class.java)

val mStateTypeName = ClassName.get(MState::class.java)
val stateTransitionTypeName = ClassName.get(StateTransition::class.java)
val stateTransitionCallbackTypeName = ClassName.get(TransitionCallback::class.java)
val emptyTransitionTypeName = ClassName.get(EmptyStateTransition::class.java)
val defaultTransitionTypeName = ClassName.get(DefaultStateTransition::class.java)
val transitionsFactoryTypeName = ClassName.get(TransitionFactory::class.java)
val viewSetTypeName = ClassName.get(ViewSet::class.java)
val navigatableTypeName = ClassName.get(Navigatable::class.java)
val navigationContextTypeName = ClassName.get(NavigationContext::class.java)
val navigationStateMachineTypeName = ClassName.get(NavigationStateMachine::class.java)
val backStackEntryTypeName = ClassName.get(BackStackEntry::class.java)
val viewContentTypeName = ClassName.get(ViewContent::class.java)
val viewContentMetaSourceTypeName = ClassName.get(ViewContentMetaSource::class.java)
val viewContentMetaTypeName = ClassName.get(ViewContentMeta::class.java)
val layerDefinitionTypeName = ClassName.get(LayerDefinition::class.java)
val subGraphTypeName = ClassName.get(SubGraph::class.java)

val runnableTypeName = ClassName.get(Runnable::class.java)
val stringTypeName = ClassName.get(String::class.java)
val serializableTypeName = ClassName.get(Serializable::class.java)
val setTypeName = ClassName.get(Set::class.java)
val hashSetTypeName = ClassName.get(HashSet::class.java)
val mapTypeName = ClassName.get(Map::class.java)
val hashMapTypeName = ClassName.get(HashMap::class.java)
val classTypeName = ClassName.get(Class::class.java)
val listTypeName = ClassName.get(List::class.java)
val collectionsTypeName = ClassName.get(Collections::class.java)
val arraysTypeName = ClassName.get(Arrays::class.java)

val contextTypeName = ClassName.get("android.content", "Context")
val logTypeName = ClassName.get("android.util", "Log")
val suppressLintType = ClassName.get("android.annotation", "SuppressLint")
val viewTypeName = ClassName.get("android.view", "View")
val viewGroupTypeName = ClassName.get("android.view", "ViewGroup")
val frameLayoutTypeName = ClassName.get("android.widget", "FrameLayout")
val parcelableClass = ClassName.get("android.os", "Parcelable")
val bundleClass = ClassName.get("android.os", "Bundle")
val drawableClass = ClassName.get("android.graphics.drawable", "Drawable")
val textWatcherClass = ClassName.get("android.text", "TextWatcher")
val editableClass = ClassName.get("android.text", "Editable")
val baseAdapterClass = ClassName.get("android.widget", "BaseAdapter")
val dataSetObserverClass = ClassName.get("android.database", "DataSetObserver")
val recyclerAdapterClass = ClassName.get("androidx.recyclerview.widget", "RecyclerView", "Adapter")
val recyclerHolderClass = ClassName.get("androidx.recyclerview.widget", "RecyclerView", "ViewHolder")
val recyclerDataObserverClass = ClassName.get("androidx.recyclerview.widget", "RecyclerView", "AdapterDataObserver")

fun TypeElement.extractViewSetType(): TypeName =
        run {
            var resultList = listOf(this)
            var type: TypeElement? = this
            while (type != null) {
                resultList = resultList + type
                type = (type.superclass as? DeclaredType)?.asElement() as? TypeElement
            }
            superclass.kind == TypeKind.DECLARED
            resultList
        }
        .flatMap { it.interfaces.orEmpty() }
        .mapNotNull { TypeName.get(it) as? ParameterizedTypeName }
        .firstOrNull { it.rawType == mStateTypeName }
        .let {
            it ?: error("Class ${qualifiedName} not implements ${mStateTypeName.canonicalName()}")
        }
        .typeArguments
        .apply { check(size == 4) }
        .get(1)
        ?: error("Internal error")

fun TypeElement.extractViewTypeFromViewContent(): TypeName =
        run {
            var resultList = listOf(this)
            var type: TypeElement? = this
            while (type != null) {
                resultList = resultList + type
                type = (type.superclass as? DeclaredType)?.asElement() as? TypeElement
            }
            superclass.kind == TypeKind.DECLARED
            resultList
        }
        .flatMap { it.interfaces.orEmpty() }
        .mapNotNull { TypeName.get(it) as? ParameterizedTypeName }
        .firstOrNull { it.rawType == viewContentTypeName }
        .let {
            it ?: error("Class ${qualifiedName} not implements ${viewContentTypeName.canonicalName()}")
        }
        .typeArguments
        .apply { check(size == 1) }
        .get(0)
        ?: error("Internal error")