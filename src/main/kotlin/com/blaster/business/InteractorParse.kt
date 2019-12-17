package com.blaster.business

import com.blaster.data.managers.kotlin.KotlinManager
import com.blaster.data.managers.statements.StatementsManager
import com.blaster.data.nodes.Node
import com.blaster.platform.LEM_COMPONENT
import java.io.File
import java.net.URL
import javax.inject.Inject

// TODO: too much common code
class InteractorParse {
    @Inject
    lateinit var kotlinManager: KotlinManager

    @Inject
    lateinit var interactorCommands: InteractorCommands

    @Inject
    lateinit var statementsManager: StatementsManager

    @Inject
    lateinit var interactorFormat: InteractorFormat

    @Inject
    lateinit var interactorStructs: InteractorStructs

    init {
        LEM_COMPONENT.inject(this)
    }

    // This call will convert a scenario file into a list of nodes. The parameters are self explanatory.
    fun parseScenario(sourceUrl: URL, sourceRoot: File, scenario: File): List<Node> {
        // First operation of this method is to convert text in the scenario file into a distinct nodes. Paragraphs are separated by the new lines
        val nodes = interactorFormat.textToNodes(scenario.readText())
        // The next task is to apply common procedures for the nodes: identification and application of commands, identification of the structures
        return renderNodes(sourceUrl, sourceRoot, nodes)
        // #include; def; com.blaster.business.InteractorParse::renderNodes
    }

    // Routine for parsing of the definitions. Accepts the sources url and root and a location of the definition. Returns a list of nodes for this definition (commentaries, code, commands, etc.)
    fun parseDef(sourceUrl: URL, sourceRoot: File, location: Location): List<Node> {
        // When the definition is located, we extract the code with the help of the ANTLR4
        val definition = kotlinManager.extractDefinition(location)
        // Next step is to split this text onto the commentaries and code snippets. We also format them - removing unused lines, spaces, etc.
        val withoutTabulation = interactorFormat.removeCommonTabulation(definition)
        val statements = statementsManager.extractStatements(withoutTabulation)
        return renderNodes(sourceUrl, sourceRoot, statements)
        // #include; def; com.blaster.business.InteractorParse::renderNodes
    }
    
    fun parseDecl(sourceUrl: URL, sourceRoot: File, location: Location): List<Node> {
        val declarations = kotlinManager.extractDeclaration(location)
        val withoutTabulation = mutableListOf<String>()
        declarations.forEach { withoutTabulation.add(interactorFormat.removeCommonTabulation(it)) }
        val statements = mutableListOf<Node>()
        declarations.forEach { statements.addAll(statementsManager.extractStatements(it)) }
        return renderNodes(sourceUrl, sourceRoot, statements)
    }

    private fun renderNodes(sourceUrl: URL, sourceRoot: File, nodes: List<Node>): List<Node> {
        val withCommands = interactorCommands.identifyCommands(sourceUrl, sourceRoot, nodes)
        val commandsApplied = interactorCommands.applyCommands(sourceUrl, sourceRoot, withCommands)
        return interactorStructs.identifyStructs(commandsApplied)
    }
}