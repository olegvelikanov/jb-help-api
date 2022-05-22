package io.github.olegvelikanov.completer

import io.github.olegvelikanov.domain.PageNotFoundException
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

class SequelConfigImpl : Config {

    override fun getCurrentVersion(productName: String): String {

        val resultRows = transaction {
            CurrentVersions
                .select { CurrentVersions.name eq productName }
                .toList()
        }
        if (resultRows.isEmpty()) {
            throw PageNotFoundException("Can't find current version for $productName")
        }
        if (resultRows.size != 1) {
            throw SQLException("More than row for primary key $productName")
        }

        return resultRows[0][CurrentVersions.version]
    }

    override fun getDefaultPageName(productName: String, productVersion: String): String {

        val resultRows = transaction {
            DefaultPages
                .select { (DefaultPages.name eq productName) and (DefaultPages.version eq productVersion) }
                .toList()
        }
        if (resultRows.isEmpty()) {
            throw PageNotFoundException("Can't find default page for (name=$productName; version=$productVersion)")
        }
        if (resultRows.size != 1) {
            throw SQLException("More than row for primary key (name=$productName; version=$productVersion)")
        }

        return resultRows[0][DefaultPages.defaultPage]

    }
}

object CurrentVersions : Table("current_versions") {
    val name: Column<String> = text("product_name")
    val version: Column<String> = text("product_version")
}

object DefaultPages : Table("default_pages") {
    val name: Column<String> = text("product_name")
    val version: Column<String> = text("product_version")
    val defaultPage: Column<String> = text("default_page")
}