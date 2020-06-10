package name.wildswift.mapache.generator

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

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