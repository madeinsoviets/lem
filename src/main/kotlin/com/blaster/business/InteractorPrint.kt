package com.blaster.business

import com.blaster.data.paragraphs.*
import com.blaster.data.managers.printing.PrintingManager
import com.blaster.platform.LEM_COMPONENT
import java.io.File
import java.lang.IllegalStateException
import javax.inject.Inject

class InteractorPrint {
    @Inject
    lateinit var interactorLocation: InteractorLocation

    @Inject
    lateinit var printingManager: PrintingManager

    init {
        LEM_COMPONENT.inject(this)
    }

    // Parameters of this function are: the output file and a list of paragraphs to be printed
    fun printArticle(output: File, paragraphs: List<Paragraph>) {
        // After receiving a list of paragraphs, we wrap them into an article template
        val article = printingManager.renderTemplate("template_article.ftlh", hashMapOf("article" to printParagraphs(paragraphs)))
        // The result is sent to printing manager to be put into a file
        printingManager.printArticle(output, article)
    }

    // This call allows us to print the body of the article - a list of paragraphs. One thing to note is that this routine can be called recursively. The style of the output will look slightly differently. This fact is reflected by the additional parameter 'child'. The result of this method is the HTML generated.
    private fun printParagraphs(paragraphs: List<Paragraph>, child: Boolean = false): String {
        // We create the variable to hold the result and then we go through the paragraphs one by one
        var result = ""
        for (paragraph in paragraphs) {
            when (paragraph) {
                // For each type we call the appropriate routine
                is ParagraphText -> result += printText(paragraph.text, child) + "\n"
                is ParagraphCode -> result += printCode(paragraph.code, child) + "\n"
                // If the paragraph is a command, we modify the result directly on place
                is ParagraphCommand -> {
                    when (paragraph.type) {
                        // It can be something related to the attributes of the page
                        ParagraphCommand.Type.HEADER -> result += printHeader(paragraph.subcommand, paragraph.argument) + "\n"
                        // Or some insert - like a reference or a picture
                        ParagraphCommand.Type.INCLUDE -> {
                            when (paragraph.subcommand) {
                                SUBCOMMAND_LINK -> result + printLink(paragraph.argument, paragraph.argument1, paragraph.argument2, child) + "\n"
                                SUBCOMMAND_PICTURE -> result + printPicture(paragraph.argument, paragraph.argument1, paragraph.argument2, child) + "\n"
                            }
                        }
                        else -> throw IllegalStateException("Unhandled command!")
                    }
                }
            }
            // Some of the paragraphs can have internal children - that happens, when for example we include the code with a command. In this case we also want to render them.
            if (paragraph.children.isNotEmpty()) {
                result += printChild((paragraph as ParagraphCommand).argument, paragraph.children) + "\n"
            }
        }
        // The final result is returned from the call. it will always contain one unnecessary '\n' character, so we cutting that out
        return result.dropLast(1)
    }

    private fun printChild(path: String, children: List<Paragraph>): String {
        return printingManager.renderTemplate(
            "template_children.ftlh", hashMapOf("path" to path, "children" to printParagraphs(children, true)))
    }

    // Here is how we print text paragraph
    private fun printText(text: String, child: Boolean): String {
        // If this text is a child of another paragraph, appropriate style is selected
        val clz = if (child) "text_child" else "text"
        // Then we select a template and pass the task to the printing manager
        return printingManager.renderTemplate("template_text.ftlh", hashMapOf("class" to clz, "text" to text))
    }

    private fun printCode(code: String, child: Boolean): String {
        val clz = if (child) "code_child" else "code"
        return printingManager.renderTemplate("template_code.ftlh", hashMapOf("class" to clz, "code" to code))
    }

    private fun printHeader(type: String, text: String): String {
        return printingManager.renderTemplate("template_header.ftlh", hashMapOf("type" to type, "header" to text))
    }

    private fun printLink(label: String, descr: String, link: String, child: Boolean): String {
        val clz = if (child) "link_child" else "link"
        return printingManager.renderTemplate(
            "template_link.ftlh", hashMapOf("class" to clz, "label" to label, "descr" to descr, "link" to link))
    }

    private fun printPicture(label: String, descr: String, link: String, child: Boolean): String {
        val clz = if (child) "picture_child" else "picture"
        return printingManager.renderTemplate(
            "template_picture.ftlh", hashMapOf("class" to clz, "label" to label, "descr" to descr, "link" to link))
    }
}