package io.github.olegvelikanov.redirect

import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPageParams

interface Redirection {
    fun requireRedirect(original: HelpPageParams, completed: CompletedHelpPageParams): String
}