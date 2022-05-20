package io.github.olegvelikanov.routes

import io.github.olegvelikanov.domain.HelpPageParams
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows


internal class HelpKtTest {

    @Test
    fun parseHelpPathTest() {
        assertAll(

            {
                assertEquals(
                    HelpPageParams("resharper/sdk", "", ""),
                    parseHelpPath("/help/resharper/sdk")
                )
            },
            {
                assertEquals(
                    HelpPageParams("resharper/sdk", "", ""),
                    parseHelpPath("/help/resharper/sdk/")
                )
            },
            {
                assertEquals(
                    HelpPageParams("resharper/sdk", "2020.1", ""),
                    parseHelpPath("/help/resharper/sdk/2020.1")
                )
            },
            {
                assertEquals(
                    HelpPageParams("resharper/sdk", "2020.1", ""),
                    parseHelpPath("/help/resharper/sdk/2020.1/")
                )
            },
            {
                assertEquals(
                    HelpPageParams("resharper/sdk", "", "index.html"),
                    parseHelpPath("/help/resharper/sdk/index.html")
                )
            },
            {
                assertEquals(
                    HelpPageParams("resharper/sdk", "2020.1", "index.html"),
                    parseHelpPath("/help/resharper/sdk/2020.1/index.html")
                )
            },
            { assertThrows<IllegalArgumentException> { parseHelpPath("/help/2020.1/index.html") } },
        )
    }
}