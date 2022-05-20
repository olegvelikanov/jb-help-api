package io.github.olegvelikanov.services

import io.github.olegvelikanov.ConverterImpl
import io.github.olegvelikanov.domain.HelpPageParams
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ConverterImplTest {

    private val converter = ConverterImpl()

    @Test
    fun parseURL_empty_path() {
        assertThrows<IllegalArgumentException> { converter.parsePath("") }
    }

    @Test
    fun parseURL_empty_path_trailing_slash() {
        assertThrows<IllegalArgumentException> { converter.parsePath("/") }
    }

    @Test
    fun parseURL_empty_product() {
        assertThrows<IllegalArgumentException> { converter.parsePath("/help") }
    }

    @Test
    fun parseURL_empty_product_trailing_slash() {
        assertThrows<IllegalArgumentException> { converter.parsePath("/help/") }
    }

    @Test
    fun parseURL_only_product() {
        assertEquals(
            HelpPageParams("idea", "", ""),
            converter.parsePath("/help/idea")
        )
    }

    @Test
    fun parseURL_only_product_trailing_slash() {
        assertEquals(
            HelpPageParams("idea", "", ""),
            converter.parsePath("/help/idea/")
        )
    }

}