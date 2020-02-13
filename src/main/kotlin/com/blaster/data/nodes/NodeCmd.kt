package com.blaster.data.nodes

import com.blaster.business.Location

const val COMMAND_IDENTIFIER    = "#"

const val COMMAND_INCLUDE       = "include"
const val COMMAND_HEADER        = "header"
const val COMMAND_PICTURE       = "picture"
const val COMMAND_OMIT          = "omit"
const val COMMAND_INLINE        = "inline"
const val COMMAND_CITE          = "cite"
const val COMMAND_CONTENT       = "content"

const val SUBCOMMAND_DECL       = "decl"
const val SUBCOMMAND_DEF        = "def"
const val SUBCOMMAND_GLSL       = "glsl"

enum class CmdType { INCLUDE, OMIT, HEADER, PICTURE, INLINE, CITE, CONTENT }

data class NodeCommand(val cmdType: CmdType, val arguments: List<String>,
                       val location: Location? = null, val children: List<Node> = listOf()) : Node {
    // TODO: I do not like the generic approach
    val subcommand: String
        get() = arguments[0]

    val argument: String
        get() = arguments[1]

    val argument1: String
        get() = arguments[2]

    val argument2: String
        get() = arguments[3]
}