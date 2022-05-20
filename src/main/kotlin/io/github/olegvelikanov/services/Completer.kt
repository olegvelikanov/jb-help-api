package io.github.olegvelikanov.services

import io.github.olegvelikanov.Storage
import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPageParams

interface Completer {
    fun completeParams(original: HelpPageParams): CompletedHelpPageParams
}

class CompleterImpl(
    private val storage: Storage
) : Completer {
    override fun completeParams(original: HelpPageParams): CompletedHelpPageParams {
        val productName = original.productName

        var version = original.productVersion
        if (version == "") {
            version = storage.getCurrentVersion(productName)
        }

        var pageName = original.pageName
        if (pageName == "") {
            pageName = storage.getDefaultPageName(productName, version)
        }

        return CompletedHelpPageParams(productName, version, pageName)

    }
}