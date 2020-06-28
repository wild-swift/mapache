package name.wildswift.mapache.generator.parsers.xml

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList

fun resolveType(typeToString: String): TypeName = when {
    typeToString.indexOf(".") >= 0 -> {
        ClassName.get(typeToString.split('.').dropLast(1).joinToString("."), typeToString.split('.').last())
    }
    typeToString == "string" -> ClassName.get(String::class.java)
    typeToString == "int" -> TypeName.INT
    typeToString == "bool" -> TypeName.BOOLEAN
    typeToString == "char" -> TypeName.CHAR
    typeToString == "byte" -> TypeName.BYTE
    typeToString == "long" -> TypeName.LONG
    typeToString == "float" -> TypeName.FLOAT
    typeToString == "double" -> TypeName.DOUBLE
    typeToString == "short" -> TypeName.SHORT
    else -> throw IllegalArgumentException("Mapache.xml file not valid")
}

fun NamedNodeMap.find(prefix: String? = null, name: String? = null): List<Node> =
        (0 until length)
                .mapNotNull {
                    item(it)
                            .takeIf { prefix == null || it.prefix == prefix }
                            ?.takeIf { name == null || it.localName == name }
                }

fun NodeList.find(prefix: String? = null, name: String? = null): List<Node> =
        (0 until length)
                .mapNotNull {
                    item(it)
                            .takeIf { prefix == null || it.prefix == prefix }
                            ?.takeIf { name == null || it.localName == name }
                }