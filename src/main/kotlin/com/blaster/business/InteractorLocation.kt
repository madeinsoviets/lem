package com.blaster.business

import java.io.File

open class Location(val file: File)

class LocationClass(file: File, val clazz: String) : Location(file) {
    override fun toString(): String = "{file: $file, class: $clazz}"
}

class LocationMember(file: File, val clazz: String, val identifier: String) : Location(file) {
    override fun toString(): String = "{file: $file, class: $clazz, identifier: $identifier}"
}

class LocationGlobal(file: File, val identifier: String) : Location(file) {
    override fun toString(): String = "{file: $file, identifier: $identifier}"
}

class InteractorLocation {
    // global method:       com.blaster.platform.LemAppKt::main
    // member in class:     com.blaster.platform.LemApp::render
    // class:               com.blaster.platform.LemApp

    fun locate(sourceRoot: File, path: String): Location {
        if (path.contains(":")) { // todo: should be something in the lines of \w+(.\w+)*(::[\w]+)?
            val count = path.count { it == ':' }
            check(count == 2) { "Syntactical error in the argument! $path" }
        }
        val clazz = extractClass(path)
        val file = locateFile(sourceRoot, clazz)
        return if (path.contains("::")) {
            val member = extractMember(path)
            if (clazz.endsWith("Kt")) {
                LocationGlobal(file, member)
            } else {
                LocationMember(file, clazz, member)
            }
        } else {
            LocationClass(file, clazz)
        }
    }

    private fun extractClass(path: String): String {
        val lastIndex = path.lastIndexOf(":")
        return path.substring(0, if (lastIndex >= 0) lastIndex - 1 else path.length)
    }

    private fun extractMember(path: String): String {
        return path.substring(path.lastIndexOf(":") + 1, path.length)
    }

    private fun locateFile(sourceRoot: File, clazz: String): File {
        var filepath = clazz.replace(".", "/")
        if (filepath.endsWith("Kt")) {
            filepath = filepath.removeSuffix("Kt")
        }
        filepath += ".kt"
        val result = File(sourceRoot, filepath)
        check(result.exists()) { "Provided class does not exists! $clazz" }
        return result
    }
}