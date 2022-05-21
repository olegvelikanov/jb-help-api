package io.github.olegvelikanov.storage

import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPage

interface HelpPageStorage {
    fun getHelpPageFor(params: CompletedHelpPageParams): HelpPage
}