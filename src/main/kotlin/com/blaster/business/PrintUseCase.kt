package com.blaster.business

import com.blaster.data.entities.Insert
import com.blaster.data.managers.printing.PrintingManager
import com.blaster.platform.LEM_COMPONENT
import javax.inject.Inject

class PrintUseCase {
    @Inject
    lateinit var locatorUseCase: LocatorUseCase

    @Inject
    lateinit var printingManager: PrintingManager

    init {
        LEM_COMPONENT.inject(this)
    }

    fun printArticle(path: String, parsed: List<Insert>) {
        val located = locatorUseCase.locate(path)
        printingManager.startArticleFor(located.file)
        for (insert in parsed) {
            printingManager.appendArticle(located.file, insert)
        }
        printingManager.finishArticleFor(located.file)
    }
}