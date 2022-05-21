package io.github.olegvelikanov.redirect

import io.github.olegvelikanov.converter.Converter
import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPageParams

class RedirectionImpl(private val urlConverter: Converter) : Redirection {
    override fun requireRedirect(original: HelpPageParams, completed: CompletedHelpPageParams): String {
        if (original.pageName != completed.pageName) {
            return urlConverter.generatePath(original.withPageName(completed.pageName))
        }

        return ""
    }
}