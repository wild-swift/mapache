package name.wildswift.mapache.generator

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.WildcardTypeName
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.graph.Navigatable
import name.wildswift.mapache.graph.StateTransition
import name.wildswift.mapache.graph.TransitionCallback
import name.wildswift.mapache.states.MState
import name.wildswift.mapache.viewsets.ViewSet

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
val viewSetTypeName = ClassName.get(ViewSet::class.java)
val navigatableTypeName = ClassName.get(Navigatable::class.java)
val navigationContextTypeName = ClassName.get(NavigationContext::class.java)
val runnableTypeName = ClassName.get(Runnable::class.java)

val contextClass = ClassName.get("android.content", "Context")
val viewClass = ClassName.get("android.view", "View")
val viewGroupClass = ClassName.get("android.view", "ViewGroup")
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
