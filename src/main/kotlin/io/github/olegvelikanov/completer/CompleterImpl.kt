package io.github.olegvelikanov.completer

import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPageParams

class CompleterImpl(
    private val config: Config
) : Completer {

    override fun completeParams(original: HelpPageParams): CompletedHelpPageParams {
        val productName = original.productName

        var version = original.productVersion
        if (version == "") {
            version = config.getCurrentVersion(productName)
        }

        var pageName = original.pageName
        if (pageName == "") {
            pageName = config.getDefaultPageName(productName, version)
        }

        return CompletedHelpPageParams(productName, version, pageName)

    }
}