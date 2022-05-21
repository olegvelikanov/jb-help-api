package io.github.olegvelikanov.config

data class Config(
    val database: DatabaseConfig,
    val s3: S3Config,
)

data class DatabaseConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val driverClassName: String,
    val maximumPoolSize: Int,
)

data class S3Config(
    val endpoint: String,
    val region: String,
    val bucket: String,
)