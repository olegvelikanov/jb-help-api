package io.github.olegvelikanov.completer

interface Config {
    fun getCurrentVersion(productName: String): String
    fun getDefaultPageName(productName: String, productVersion: String): String
}