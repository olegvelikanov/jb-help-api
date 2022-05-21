package io.github.olegvelikanov.routes

import io.github.olegvelikanov.completer.Completer
import io.github.olegvelikanov.converter.Converter
import io.github.olegvelikanov.domain.NoSuchProductException
import io.github.olegvelikanov.redirect.Redirection
import io.github.olegvelikanov.storage.HelpPageStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*

const val helpRoutePrefix: String = "/help"

fun Application.helpRoutes(
    converter: Converter,
    completer: Completer,
    redirection: Redirection,
    storage: HelpPageStorage
) {
    routing {
        route("$helpRoutePrefix/{...}") {
            get {
                try {
                    val helpPageParams = converter.parsePath(call.request.path())

                    val completedParams = completer.completeParams(helpPageParams)

                    val location = redirection.requireRedirect(helpPageParams, completedParams)
                    if (location != "") {
                        call.respondRedirect(location)
                        return@get
                    }

                    val helpPage = storage.getHelpPageFor(completedParams)
                    call.respondBytes(helpPage.htmlContent, contentType = ContentType.Text.Html)

                } catch (e: NoSuchProductException) {
                    call.respondPageNotFound()
                }
            }
        }
    }
}

suspend fun ApplicationCall.respondPageNotFound() {
    respondHtml(HttpStatusCode.NotFound) {
        head {
            title {
                +"Error 404"
            }
        }
        body {
            h1 {
                +"UH-OH! 404"
            }
            p {
                +"You surely know what this means."
            }
        }
    }
}


