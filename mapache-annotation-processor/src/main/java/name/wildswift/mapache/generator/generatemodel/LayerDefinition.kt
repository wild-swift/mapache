package name.wildswift.mapache.generator.generatemodel

import com.squareup.javapoet.TypeName

data class LayerDefinition(
        val initialStateWrapperType: TypeName,
        val contentIdClass: TypeName,
        val contentId: String
) {
}