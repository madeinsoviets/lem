package com.blaster.business

import com.blaster.data.inserts.Insert
import com.blaster.data.inserts.InsertCommand
import com.blaster.data.managers.lexing.LexingManager
import com.blaster.data.managers.parsing.ParsingManager
import com.blaster.platform.LEM_COMPONENT
import org.antlr.v4.runtime.ParserRuleContext
import javax.inject.Inject
import kotlin.collections.ArrayList

const val INCLUDE_PREFIX = "// include "
const val DEF_PREFIX = "def "
const val DECL_PREFIX = "decl "

class InteractorParse {
    @Inject
    lateinit var interactorLocation: InteractorLocation

    @Inject
    lateinit var lexingManager: LexingManager

    @Inject
    lateinit var parsingManager: ParsingManager

    init {
        LEM_COMPONENT.inject(this)
    }

    fun parseDef(path: String): List<Insert> {
        val location = interactorLocation.locate(path)
        val (tokenStream, parser) = lexingManager.provideParser(location.file)
        parser.reset()
        val statements = when (location) {
            is LocationGlobal -> parsingManager.locateGlobalMethodStatements(tokenStream, parser, location)
            is LocationMember -> parsingManager.locateMemberMethodStatements(tokenStream, parser, location)
            else -> throw UnsupportedOperationException()
        }
        val tokens = tokenStream.getTokens(statements.start.tokenIndex + 1, statements.stop.tokenIndex - 1)
        val inserts = ExtractorStatements(tokens).extractStatements()
        return processCommands(inserts)
    }

    private fun parseDecl(path: String): List<Insert> {
        val location = interactorLocation.locate(path)
        val (tokenStream, parser) = lexingManager.provideParser(location.file)
        parser.reset()
        val declarations = when (location) {
            is LocationGlobal -> listOf(parsingManager.locateGlobalMethodDecl(tokenStream, parser, location))
            is LocationMember -> listOf(parsingManager.locateMemberDecl(tokenStream, parser, location))
            is LocationClass -> parsingManager.locateClassDecl(tokenStream, parser, location)
            else -> throw UnsupportedOperationException()
        }
        val inserts = ArrayList<Insert>()
        for (declaration in declarations) {
            inserts.addAll(ExtractorDeclarations().extractDeclaration(tokenStream, declaration))
        }
        return processCommands(inserts)
    }

    private fun processCommands(inserts: List<Insert>): List<Insert> {
        val result = ArrayList<Insert>()
        for (insert in inserts) {
            if (insert is InsertCommand) {
                result.addAll(processCommand(insert.command))
            } else {
                result.add(insert)
            }
        }
        return result
    }

    private fun processCommand(command: String): List<Insert> {
        check(command.startsWith(INCLUDE_PREFIX)) { "Expected command is not started with expected prefix!" }
        val include = command.removePrefix(INCLUDE_PREFIX)
        return processInclude(include)
    }

    private fun processInclude(include: String): List<Insert> {
        if (include.startsWith(DECL_PREFIX)) {
            val decl = include.removePrefix(DECL_PREFIX)
            return parseDecl(decl)
        }
        if (include.startsWith(DEF_PREFIX)) {
            val def = include.removePrefix(DEF_PREFIX)
            return parseDef(def)
        }
        throw IllegalStateException("Wtf??")
    }
}