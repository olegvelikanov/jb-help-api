package io.github.olegvelikanov.completer

import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPageParams
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals

internal class CompleterImplTest {

    private val completer: Completer

    init {
        val config: Config = mockk()
        every { config.getCurrentVersion("idea") } returns "2022.1"
        every { config.getDefaultPageName("idea", "2022.1") } returns "getting-started.html"
        every { config.getDefaultPageName("idea", "2019.1") } returns "getting-started-2019.html"

        every { config.getCurrentVersion("resharper.sdk") } returns "dev"
        every { config.getDefaultPageName("resharper.sdk", "dev") } returns "welcome.html"

        completer = CompleterImpl(config)
    }


    @Test
    fun completeParams() {
        assertAll(
            {
                assertEquals(
                    CompletedHelpPageParams("idea", "2022.1", "getting-started.html"),
                    completer.completeParams(HelpPageParams("idea", "", ""))
                )
            },
            {
                assertEquals(
                    CompletedHelpPageParams("resharper.sdk", "dev", "welcome.html"),
                    completer.completeParams(HelpPageParams("resharper.sdk", "", ""))
                )
            },
            {
                assertEquals(
                    CompletedHelpPageParams("idea", "2019.1", "getting-started-2019.html"),
                    completer.completeParams(HelpPageParams("idea", "2019.1", ""))
                )
            },
            {
                assertEquals(
                    CompletedHelpPageParams("idea", "2018.1", "welcome.html"),
                    completer.completeParams(HelpPageParams("idea", "2018.1", "welcome.html"))
                )
            },
        )


    }
}