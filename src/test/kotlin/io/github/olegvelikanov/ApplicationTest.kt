package io.github.olegvelikanov

import io.github.olegvelikanov.completer.Completer
import io.github.olegvelikanov.converter.Converter
import io.github.olegvelikanov.converter.RegexConverterImpl
import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPage
import io.github.olegvelikanov.domain.HelpPageParams
import io.github.olegvelikanov.domain.PageNotFoundException
import io.github.olegvelikanov.redirect.Redirection
import io.github.olegvelikanov.redirect.RedirectionImpl
import io.github.olegvelikanov.storage.HelpPageStorage
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApplicationTest {

    private val converter: Converter = RegexConverterImpl()
    private val completer: Completer = mockk()
    private val redirection: Redirection = RedirectionImpl(converter)
    private val storage: HelpPageStorage = mockk()

    init {
        val completerMockData = mapOf(
            HelpPageParams("idea", "", "")
                    to CompletedHelpPageParams("idea", "2022.1", "getting-started.html"),

            HelpPageParams("idea", "", "getting-started.html")
                    to CompletedHelpPageParams("idea", "2022.1", "getting-started.html"),

            HelpPageParams("idea", "2022.1", "")
                    to CompletedHelpPageParams("idea", "2022.1", "getting-started.html"),

            HelpPageParams("idea", "2022.1", "getting-started.html")
                    to CompletedHelpPageParams("idea", "2022.1", "getting-started.html"),

            HelpPageParams("resharper.sdk", "", "")
                    to CompletedHelpPageParams("resharper.sdk", "", "welcome.html"),

            HelpPageParams("resharper.sdk", "", "welcome.html")
                    to CompletedHelpPageParams("resharper.sdk", "", "welcome.html"),

            HelpPageParams("resharper.sdk", "2022.2", "")
                    to CompletedHelpPageParams("resharper.sdk", "2022.2", "welcome.html"),

            HelpPageParams("resharper.sdk", "2022.2", "welcome.html")
                    to CompletedHelpPageParams("resharper.sdk", "2022.2", "welcome.html"),
        )
        mockCompleteParams(completer, completerMockData)
        every {
            completer.completeParams(
                eq(
                    HelpPageParams(
                        "go",
                        "",
                        ""
                    )
                )
            )
        } throws PageNotFoundException("Can't complete")

        every {
            storage.getHelpPageFor(eq(CompletedHelpPageParams("idea", "2022.1", "getting-started.html")))
        } returns HelpPage("idea 2022.1 getting-started.html".toByteArray())
        every {
            storage.getHelpPageFor(eq(CompletedHelpPageParams("resharper.sdk", "", "welcome.html")))
        } returns HelpPage("resharper.sdk empty version welcome.html".toByteArray())
        every {
            storage.getHelpPageFor(eq(CompletedHelpPageParams("resharper.sdk", "2022.2", "welcome.html")))
        } returns HelpPage("resharper.sdk 2022.2 welcome.html".toByteArray())

    }

    private fun mockCompleteParams(
        mock: Completer,
        data: Map<HelpPageParams, CompletedHelpPageParams>
    ) {
        data.forEach {
            every {
                mock.completeParams(eq(it.key))
            } returns it.value

        }
    }

    @Test
    fun testHelp() = testApplication {
        application {
            bootstrap(converter, completer, redirection, storage)
        }
        val client = HttpClient(client.engine) {
            followRedirects = false
        }

        var response: HttpResponse = client.get("/help/idea/")
        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/help/idea/getting-started.html", response.headers["Location"])

        response = client.get("/help/idea/getting-started.html")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("idea 2022.1 getting-started.html", response.bodyAsText())

        response = client.get("/help/idea/2022.1/")
        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/help/idea/2022.1/getting-started.html", response.headers["Location"])

        response = client.get("/help/idea/2022.1/getting-started.html")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("idea 2022.1 getting-started.html", response.bodyAsText())

        response = client.get("/help/resharper/sdk/")
        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/help/resharper/sdk/welcome.html", response.headers["Location"])

        response = client.get("/help/resharper/sdk/welcome.html")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("resharper.sdk empty version welcome.html", response.bodyAsText())

        response = client.get("/help/resharper/sdk/2022.2")
        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/help/resharper/sdk/2022.2/welcome.html", response.headers["Location"])

        response = client.get("/help/resharper/sdk/2022.2/welcome.html")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("resharper.sdk 2022.2 welcome.html", response.bodyAsText())

        response = client.get("/help/go")
        assertEquals(HttpStatusCode.NotFound, response.status)

    }
}