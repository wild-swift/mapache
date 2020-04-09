package name.wildswift.mapache.generator

import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList

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