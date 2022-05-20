package io.github.olegvelikanov.domain


data class HelpPageParams(
    val productName: String,
    val productVersion: String,
    val pageName: String
) {

    fun withPageName(pageName: String): HelpPageParams {
        return this.copy(pageName = pageName)
    }

}
