package io.github.olegvelikanov.converter

import io.github.olegvelikanov.domain.HelpPageParams
import io.github.olegvelikanov.domain.PageNotFoundException
import io.github.olegvelikanov.helpRoutePrefix
import java.util.regex.Pattern


class RegexConverterImpl : Converter {

    private val helpPathPattern: Pattern =
        Pattern.compile("^$helpRoutePrefix((?:/\\w+)+)(?:/(\\d+\\.\\d+))?(?:/([\\w\\-_]+\\.\\w+))?/?\$")

    override fun parsePath(url: String): HelpPageParams {
        val matcher = helpPathPattern.matcher(url)
        if (!matcher.matches()) {
            throw PageNotFoundException("Invalid help path")
        }

        val staticPageName = matcher.group(3) ?: ""
        val productVersion = matcher.group(2) ?: ""
        val productName = matcher.group(1).trim('/').replace('/', '.')

        return HelpPageParams(productName, productVersion, staticPageName)
    }

    override fun generatePath(params: HelpPageParams): String {
        val sb = StringBuilder()
        sb.append(helpRoutePrefix)
        if (params.productName.isBlank()) {
            throw IllegalArgumentException("Product name can't be empty")
        }
        sb.append('/')
        sb.append(params.productName.replace('.', '/'))
        if (params.productVersion.isNotBlank()) {
            sb.append('/')
            sb.append(params.productVersion)
        }
        if (params.pageName.isNotBlank()) {
            sb.append('/')
            sb.append(params.pageName)
        }
        return sb.toString()
    }
}