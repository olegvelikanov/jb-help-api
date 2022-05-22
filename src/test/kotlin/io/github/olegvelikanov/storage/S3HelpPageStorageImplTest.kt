package io.github.olegvelikanov.storage

import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPage
import io.github.olegvelikanov.domain.PageNotFoundException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.core.sync.ResponseTransformer
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.services.s3.model.NoSuchKeyException

internal class S3HelpPageStorageImplTest {

    private val storage: HelpPageStorage

    init {
        val s3Client: S3Client = mockk()

        val ideaGettingStartedRsp: ResponseBytes<GetObjectResponse> = mockk()
        every {
            s3Client.getObject(
                eq(
                    GetObjectRequest.builder().bucket("help").key("idea/2022.1/getting-started.html").build()
                ), ResponseTransformer.toBytes()
            )
        } returns ideaGettingStartedRsp
        every { ideaGettingStartedRsp.asByteArray() } returns "idea 2022.1 getting-started.html".toByteArray()

        val resharperIntroductionIndexRsp: ResponseBytes<GetObjectResponse> = mockk()
        every {
            s3Client.getObject(
                eq(
                    GetObjectRequest.builder().bucket("help").key("resharper/2022.2/Introduction__Index.html").build()
                ), ResponseTransformer.toBytes()
            )
        } returns resharperIntroductionIndexRsp
        every { resharperIntroductionIndexRsp.asByteArray() } returns "resharper 2022.2 Introduction__Index.html".toByteArray()

        val resharperSdkWelcomeRsp: ResponseBytes<GetObjectResponse> = mockk()
        every {
            s3Client.getObject(
                eq(
                    GetObjectRequest.builder().bucket("help").key("resharper.sdk/welcome.html").build()
                ), ResponseTransformer.toBytes()
            )
        } returns resharperSdkWelcomeRsp
        every { resharperSdkWelcomeRsp.asByteArray() } returns "resharper.sdk empty version welcome.html".toByteArray()

        every {
            s3Client.getObject(
                eq(
                    GetObjectRequest.builder().bucket("help").key("idea/2022.1/not-existing-page.html").build()
                ), ResponseTransformer.toBytes()
            )
        } throws NoSuchKeyException.builder().build()

        storage = S3HelpPageStorageImpl(s3Client, "help")
    }

    @Test
    fun getHelpPageFor() {
        assertEquals(
            HelpPage("idea 2022.1 getting-started.html".toByteArray()),
            storage.getHelpPageFor(
                CompletedHelpPageParams(
                    "idea",
                    "2022.1",
                    "getting-started.html"
                )
            )
        )
        assertEquals(
            HelpPage("resharper 2022.2 Introduction__Index.html".toByteArray()),
            storage.getHelpPageFor(
                CompletedHelpPageParams(
                    "resharper",
                    "2022.2",
                    "Introduction__Index.html"
                )
            )
        )
        assertEquals(
            HelpPage("resharper.sdk empty version welcome.html".toByteArray()),
            storage.getHelpPageFor(
                CompletedHelpPageParams(
                    "resharper.sdk",
                    "",
                    "welcome.html"
                )
            )
        )

        assertThrows<PageNotFoundException> {
            storage.getHelpPageFor(
                CompletedHelpPageParams(
                    "idea",
                    "2022.1",
                    "not-existing-page.html"
                )
            )
        }
    }
}