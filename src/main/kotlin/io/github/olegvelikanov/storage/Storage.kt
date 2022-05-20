package io.github.olegvelikanov


interface Storage {
    fun getCurrentVersion(productName: String): String
    fun getDefaultPageName(productName: String, productVersion: String): String
    fun getHelpPageFor(productName: String, productVersion: String, helpPageName: String)
}

class SequalStorageImpl: Storage {
    override fun getCurrentVersion(productName: String): String {
        TODO("Not yet implemented")
    }

    override fun getDefaultPageName(productName: String, productVersion: String): String {
        TODO("Not yet implemented")
    }

    override fun getHelpPageFor(productName: String, productVersion: String, helpPageName: String) {
        TODO("Not yet implemented")
    }
}