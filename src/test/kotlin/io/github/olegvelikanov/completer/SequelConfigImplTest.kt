package io.github.olegvelikanov.completer

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.olegvelikanov.domain.PageNotFoundException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.sql.SQLException
import kotlin.test.assertEquals

internal class SequelConfigImplTest {

    private val config = SequelConfigImpl()

    @Test
    fun getCurrentVersion() {
        assertThrows<SQLException> { config.getCurrentVersion("somehow_doubled_name") }

        assertThrows<PageNotFoundException> { config.getCurrentVersion("goland") }
        assertEquals("2022.1", config.getCurrentVersion("idea"))
        assertEquals("2022.2", config.getCurrentVersion("resharper"))
        assertEquals("", config.getCurrentVersion("resharper.sdk"))

    }

    @Test
    fun getDefaultPageName() {
        assertThrows<SQLException> { config.getDefaultPageName("somehow_doubled_name", "somehow_doubled_version") }

        assertThrows<PageNotFoundException> { config.getDefaultPageName("goland", "") }
        assertThrows<PageNotFoundException> { config.getDefaultPageName("idea", "2030.1") }

        assertEquals("getting-started.html", config.getDefaultPageName("idea", "2022.1"))
        assertEquals("Introduction__Index.html", config.getDefaultPageName("resharper", "2022.1"))
        assertEquals("welcome.html", config.getDefaultPageName("resharper.sdk", ""))
    }

    companion object {
        @BeforeAll
        @JvmStatic
        internal fun setup() {
            connectH2ForTest()
            createTables()
            fillWithBasicData()
        }

        private fun connectH2ForTest() {
            val dataSource = HikariDataSource(HikariConfig().apply {
                jdbcUrl = "jdbc:h2:mem:;DATABASE_TO_UPPER=false;MODE=PostgreSQL"
                driverClassName = "org.h2.Driver"
                maximumPoolSize = 2
            })
            Database.connect(dataSource)
        }

        private fun createTables() {
            transaction {
                SchemaUtils.create(CurrentVersions)
                SchemaUtils.create(DefaultPages)
            }
        }

        private fun fillWithBasicData() {
            transaction {
                CurrentVersions.insert {
                    it[name] = "idea"
                    it[version] = "2022.1"
                }
                CurrentVersions.insert {
                    it[name] = "resharper"
                    it[version] = "2022.2"
                }
                CurrentVersions.insert {
                    it[name] = "resharper.sdk"
                    it[version] = ""
                }
                CurrentVersions.insert {
                    it[name] = "somehow_doubled_name"
                    it[version] = ""
                }
                CurrentVersions.insert {
                    it[name] = "somehow_doubled_name"
                    it[version] = ""
                }

                DefaultPages.insert {
                    it[name] = "idea"
                    it[version] = "2022.1"
                    it[defaultPage] = "getting-started.html"
                }
                DefaultPages.insert {
                    it[name] = "resharper"
                    it[version] = "2022.1"
                    it[defaultPage] = "Introduction__Index.html"
                }
                DefaultPages.insert {
                    it[name] = "resharper.sdk"
                    it[version] = ""
                    it[defaultPage] = "welcome.html"
                }
                DefaultPages.insert {
                    it[name] = "somehow_doubled_name"
                    it[version] = "somehow_doubled_version"
                    it[defaultPage] = ""
                }
                DefaultPages.insert {
                    it[name] = "somehow_doubled_name"
                    it[version] = "somehow_doubled_version"
                    it[defaultPage] = ""
                }
            }
        }
    }
}