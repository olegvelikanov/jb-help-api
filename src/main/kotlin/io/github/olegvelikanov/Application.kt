package io.github.olegvelikanov

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.olegvelikanov.completer.Completer
import io.github.olegvelikanov.completer.CompleterImpl
import io.github.olegvelikanov.completer.SequelConfigImpl
import io.github.olegvelikanov.config.Config
import io.github.olegvelikanov.converter.Converter
import io.github.olegvelikanov.converter.RegexConverterImpl
import io.github.olegvelikanov.domain.PageNotFoundException
import io.github.olegvelikanov.redirect.Redirection
import io.github.olegvelikanov.redirect.RedirectionImpl
import io.github.olegvelikanov.storage.HelpPageStorage
import io.github.olegvelikanov.storage.S3HelpPageStorageImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.Database
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

const val helpRoutePrefix = "/help"

fun main() {
    // TODO: so far DI is not needed, but in the future it is worth considering its introduction
    val config = ConfigLoaderBuilder.default().addResourceSource("/application.yml").build().loadConfigOrThrow<Config>()
    val dataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl = config.database.jdbcUrl
        driverClassName = config.database.driverClassName
        username = config.database.username
        password = config.database.password
        maximumPoolSize = config.database.maximumPoolSize
    })
    Database.connect(dataSource)
    val s3BucketName = config.s3.bucket
    val s3Region = Region.of(config.s3.region)
    val s3Client = S3Client.builder().region(s3Region).endpointOverride(URI(config.s3.endpoint)).build()
    val completerConfig = SequelConfigImpl()
    val completer = CompleterImpl(completerConfig)
    val converter = RegexConverterImpl()
    val redirection = RedirectionImpl(converter)
    val storage = S3HelpPageStorageImpl(s3Client, s3BucketName)

    embeddedServer(Netty, port = 8080) {
        bootstrap(converter, completer, redirection, storage)
    }.start(wait = true)
}

fun Application.bootstrap(
    converter: Converter,
    completer: Completer,
    redirection: Redirection,
    storage: HelpPageStorage
) {
    install(IgnoreTrailingSlash)
    helpRoutes(converter, completer, redirection, storage)
}

fun Application.helpRoutes(
    converter: Converter, completer: Completer, redirection: Redirection, storage: HelpPageStorage
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

                } catch (e: PageNotFoundException) {
                    call.respondPageNotFound()
                }
            }
        }
    }
}

// TODO: Most probably 404 page also should be taken from s3 storage.
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
