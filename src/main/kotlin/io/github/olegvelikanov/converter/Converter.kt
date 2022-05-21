package io.github.olegvelikanov.converter

import io.github.olegvelikanov.domain.HelpPageParams

interface Converter {
    fun parsePath(url: String): HelpPageParams
    fun generatePath(params: HelpPageParams): String
}