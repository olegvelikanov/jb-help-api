package io.github.olegvelikanov.services

import io.github.olegvelikanov.Converter
import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPageParams

interface RedirectProvider {
    fun requireRedirect(original: HelpPageParams, completed: CompletedHelpPageParams): String
}

class RedirectImpl(
    private val urlConverter: Converter
) : RedirectProvider {

    override fun requireRedirect(original: HelpPageParams, completed: CompletedHelpPageParams): String {
        if (original.pageName != completed.pageName) {
            return urlConverter.generatePath(original.withPageName(completed.pageName))
        }

        return ""
    }

}