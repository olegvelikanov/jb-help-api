package io.github.olegvelikanov.redirect

import io.github.olegvelikanov.converter.Converter
import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPageParams
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals

internal class RedirectionImplTest {

    private val redirection: Redirection

    init {
        val converter: Converter = mockk()

        every {
            converter.generatePath(
                eq(
                    HelpPageParams(
                        "idea",
                        "",
                        "welcome.html"
                    )
                )
            )
        } returns "/help/idea/welcome.html"

        redirection = RedirectionImpl(converter)
    }

    @Test
    fun requireRedirect() {
        assertAll(
            {
                assertEquals(
                    "",
                    redirection.requireRedirect(
                        HelpPageParams("idea", "", "welcome.html"),
                        CompletedHelpPageParams("idea", "2022.1", "welcome.html")
                    )
                )
            },
            {
                assertEquals(
                    "/help/idea/welcome.html",
                    redirection.requireRedirect(
                        HelpPageParams("idea", "", ""),
                        CompletedHelpPageParams("idea", "2022.1", "welcome.html")
                    )
                )
            }

        )

    }
}