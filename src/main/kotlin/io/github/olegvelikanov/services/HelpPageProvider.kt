package io.github.olegvelikanov.services

import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPage

interface HelpPageProvider {
    fun getHelpPage(params: CompletedHelpPageParams): HelpPage
}


class HelpPageImpl : HelpPageProvider {
    override fun getHelpPage(params: CompletedHelpPageParams): HelpPage {
        TODO("Not yet implemented")
    }
}


