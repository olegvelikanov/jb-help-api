package io.github.olegvelikanov.domain

data class CompletedHelpPageParams(
    val productName: String,
    val productVersion: String,
    val pageName: String
) {
    init {
        productName.requireNotBlank("Product name can't be blank")
        productVersion.requireNotBlank("Product version can't be blank")
        productName.requireNotBlank("Page name can't be blank")
    }
}

fun String.requireNotBlank(errMsg: String) {
    if (isBlank()) {
        throw IllegalArgumentException(errMsg)
    }
}