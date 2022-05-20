package io.github.olegvelikanov

import io.github.olegvelikanov.routes.helpRoutes
import io.github.olegvelikanov.services.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main() {

    val storage = SequalStorageImpl()
    val converter = ConverterImpl()
    val completer = CompleterImpl(storage)
    val redirectProvider = RedirectImpl(converter)
    val helpPageProvider: HelpPageProvider = HelpPageImpl()

    embeddedServer(Netty, port = 8080) {
        install(IgnoreTrailingSlash)
        helpRoutes(converter, completer, redirectProvider, helpPageProvider)
    }.start(wait = true)
}