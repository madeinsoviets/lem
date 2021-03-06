package com.blaster.platform

import com.blaster.business.*
import com.blaster.data.managers.printing.PrintingManagerImpl
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [LemModule::class])
interface LemComponent {
    fun inject(lemApp: LemApp)
    fun inject(interactorParse: InteractorParse)
    fun inject(interactorPrint: InteractorPrint)
    fun inject(printingManagerImpl: PrintingManagerImpl)
    fun inject(interactorLocation: InteractorLocation)
    fun inject(interactorTokens: InteractorTokens)
}

val LEM_COMPONENT: LemComponent = DaggerLemComponent.builder().build()