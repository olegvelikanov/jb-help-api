package io.github.olegvelikanov

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.olegvelikanov.completer.CompleterImpl
import io.github.olegvelikanov.completer.SequelConfigImpl
import io.github.olegvelikanov.config.Config
import io.github.olegvelikanov.converter.RegexConverterImpl
import io.github.olegvelikanov.redirect.RedirectionImpl
import io.github.olegvelikanov.routes.helpRoutes
import io.github.olegvelikanov.storage.S3HelpPageStorageImpl
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

fun main() {


    val config = ConfigLoaderBuilder.default()
        .addResourceSource("/application.yml")
        .build()
        .loadConfigOrThrow<Config>()


    val dataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl         = config.database.jdbcUrl
        driverClassName = config.database.driverClassName
        username        = config.database.username
        password        = config.database.password
        maximumPoolSize = config.database.maximumPoolSize
    })
    Database.connect(dataSource)

    val completerConfig = SequelConfigImpl()
    val completer = CompleterImpl(completerConfig)

    val converter = RegexConverterImpl()

    val redirection = RedirectionImpl(converter)

    val storage = S3HelpPageStorageImpl(config.s3)

    embeddedServer(Netty, port = 8080) {
        install(IgnoreTrailingSlash)
        helpRoutes(converter, completer, redirection, storage)
    }.start(wait = true)
}