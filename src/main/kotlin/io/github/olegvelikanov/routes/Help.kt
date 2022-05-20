package io.github.olegvelikanov.routes

import io.github.olegvelikanov.Converter
import io.github.olegvelikanov.services.Completer
import io.github.olegvelikanov.services.HelpPageProvider
import io.github.olegvelikanov.services.RedirectProvider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val helpRoutePrefix: String = "/help"

fun Application.helpRoutes(
    converter: Converter,
    completer: Completer,
    redirectProvider: RedirectProvider,
    helpPageProvider: HelpPageProvider
) {
    routing {
        route("$helpRoutePrefix/{...}") {
            get {
                val helpPageParams = converter.parsePath(call.request.path())

                val completedParams = completer.completeParams(helpPageParams)

                val location = redirectProvider.requireRedirect(helpPageParams, completedParams)
                if (location != "") {
                    call.respondRedirect(location)
                }

                val helpPage = helpPageProvider.getHelpPage(completedParams)
                call.respondText(helpPage.htmlContent, contentType = ContentType.Text.Html)

            }
        }
    }
}


