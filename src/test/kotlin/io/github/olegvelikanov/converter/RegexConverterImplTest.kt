package io.github.olegvelikanov.converter

import io.github.olegvelikanov.domain.HelpPageParams
import io.github.olegvelikanov.domain.PageNotFoundException
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class RegexConverterImplTest {

    private val converter = RegexConverterImpl()

    @Test
    fun parseURL_empty_path() {
        assertAll(
            { assertThrows<PageNotFoundException> { converter.parsePath("") } },
            { assertThrows<PageNotFoundException> { converter.parsePath("/") } },
            { assertThrows<PageNotFoundException> { converter.parsePath("/help") } },
            { assertThrows<PageNotFoundException> { converter.parsePath("/help/") } },
            {
                assertEquals(
                    HelpPageParams("idea", "", ""),
                    converter.parsePath("/help/idea")
                )
            },
            {
                assertEquals(
                    HelpPageParams("idea", "", ""),
                    converter.parsePath("/help/idea/")
                )
            },
            {
                assertEquals(
                    HelpPageParams("idea", "", ""),
                    converter.parsePath("/help/idea/")
                )
            },
            {
                assertEquals(
                    HelpPageParams("resharper.sdk", "2020.1", ""),
                    converter.parsePath("/help/resharper/sdk/2020.1")
                )
            },
            {
                assertEquals(
                    HelpPageParams("resharper.sdk", "2020.1", ""),
                    converter.parsePath("/help/resharper/sdk/2020.1/")
                )
            },
            {
                assertEquals(
                    HelpPageParams("resharper.sdk", "", "index.html"),
                    converter.parsePath("/help/resharper/sdk/index.html")
                )
            },
            {
                assertEquals(
                    HelpPageParams("resharper.sdk", "2020.1", "index.html"),
                    converter.parsePath("/help/resharper/sdk/2020.1/index.html")
                )
            },
        )
    }

    @Test
    fun generatePath() {
        assertAll(
            { assertThrows<IllegalArgumentException> { converter.generatePath(HelpPageParams("", "", "")) } },
            {
                assertEquals(
                    "/help/idea",
                    converter.generatePath(HelpPageParams("idea", "", ""))
                )
            },
            {
                assertEquals(
                    "/help/idea/2022.1",
                    converter.generatePath(HelpPageParams("idea", "2022.1", ""))
                )
            },
            {
                assertEquals(
                    "/help/idea/welcome.html",
                    converter.generatePath(HelpPageParams("idea", "", "welcome.html"))
                )
            },
            {
                assertEquals(
                    "/help/resharper/sdk/welcome.html",
                    converter.generatePath(HelpPageParams("resharper.sdk", "", "welcome.html"))
                )
            },
            {
                assertEquals(
                    "/help/resharper/sdk/2022.1/welcome.html",
                    converter.generatePath(HelpPageParams("resharper.sdk", "2022.1", "welcome.html"))
                )
            },
        )
    }
}