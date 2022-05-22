package io.github.olegvelikanov.domain

data class HelpPage(
    val htmlContent: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HelpPage

        if (!htmlContent.contentEquals(other.htmlContent)) return false

        return true
    }

    override fun hashCode(): Int {
        return htmlContent.contentHashCode()
    }
}