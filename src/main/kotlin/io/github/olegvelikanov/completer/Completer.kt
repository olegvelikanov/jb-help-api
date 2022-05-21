package io.github.olegvelikanov.completer

import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPageParams

interface Completer {
    fun completeParams(original: HelpPageParams): CompletedHelpPageParams
}