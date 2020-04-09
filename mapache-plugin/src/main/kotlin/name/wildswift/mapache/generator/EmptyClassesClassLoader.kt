package name.wildswift.mapache.generator

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class EmptyClassesClassLoader(parent: ClassLoader): ClassLoader(parent) {
    override fun findClass(className: String): Class<*> {
        try {
            return super.findClass(className)
        } catch (e: ClassNotFoundException) {
            if (className.startsWith("java.")) throw e
            if (className.startsWith("java\$")) throw e
            if (className.startsWith("groovy.")) throw e
            if (className.startsWith("groovy\$")) throw e
            if (className.indexOf("\$") >= 0) {
                throw e
            }
        }

        val data = ByteArrayOutputStream().apply {
            writeEmptyClassData(className, DataOutputStream(this))
        }.toByteArray()
        return defineClass(className, data, 0, data.size)
    }


    /**
     * Generate bytecode for empty class without parent or interfaces
     */
    private fun writeEmptyClassData(className: String, out: DataOutputStream) {
        // magic
        out.writeInt(0xCAFEBABE.toInt())
        // version
        out.writeInt(0x33)
        // constants (count)
        out.writeShort(0xA)
        // className (1)
        out.writeByte(1)
        val classNameToBc = className.replace(".", "/", false).toByteArray(Charsets.UTF_8)
        out.writeShort(classNameToBc.size)
        out.write(classNameToBc)
        // this class constant (2)
        out.writeByte(7)
        out.writeShort(1)
        // parent class name (3)
        out.writeByte(1)
        out.writeShort("java/lang/Object".length)
        out.write("java/lang/Object".toByteArray(Charsets.UTF_8))
        // parent class constant (4)
        out.writeByte(7)
        out.writeShort(3)
        // constructor name (5)
        out.writeByte(1)
        out.writeShort("<init>".length)
        out.write("<init>".toByteArray(Charsets.UTF_8))
        // constructor signature (6)
        out.writeByte(1)
        out.writeShort("()V".length)
        out.write("()V".toByteArray(Charsets.UTF_8))
        // constructor name and type (7)
        out.writeByte(12)
        out.writeShort(5)
        out.writeShort(6)
        // parent constructor ref (8)
        out.writeByte(10)
        out.writeShort(4)
        out.writeShort(7)
        // Code String (9)
        out.writeByte(1)
        out.writeShort("Code".length)
        out.write("Code".toByteArray(Charsets.UTF_8))
        // access flags
        out.writeShort(0x21)
        // this class
        out.writeShort(0x2)
        // parent class
        out.writeShort(0x4)
        // interfaces and fields count
        out.writeShort(0)
        out.writeShort(0)
        // Methods (constructor only)
        out.writeShort(1)
        // access, name, descriptor
        out.writeShort(0x1)
        out.writeShort(0x5)
        out.writeShort(0x6)
        // method attributes
        out.writeShort(0x1)
        // name, size, max stack, max locals
        out.writeShort(0x9)
        out.writeInt(0x11)
        out.writeShort(1)
        out.writeShort(1)
        // Code stub
        out.writeInt(5)
        out.writeInt(0x2AB70008)
        out.writeByte(0xB1)
        // Exceptions count
        out.writeShort(0)
        // attributes
        out.writeShort(0)
        // attributes of class
        out.writeShort(0)
    }

}