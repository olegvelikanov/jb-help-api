package io.github.olegvelikanov.storage

import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPage
import io.github.olegvelikanov.domain.PageNotFoundException
import software.amazon.awssdk.core.sync.ResponseTransformer
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException

class S3HelpPageStorageImpl(private val s3Client: S3Client, private val s3BucketName: String) : HelpPageStorage {

    override fun getHelpPageFor(params: CompletedHelpPageParams): HelpPage {
        val s3Key = convertIntoS3Key(params)
        val getRequest = GetObjectRequest.builder()
            .bucket(s3BucketName)
            .key(s3Key)
            .build()
        try {
            val responseBytes = s3Client.getObject(getRequest, ResponseTransformer.toBytes())
            return HelpPage(responseBytes.asByteArray())
        } catch (e: NoSuchKeyException) {
            throw PageNotFoundException("File not found in s3 for key='$s3Key'")
        }
    }

    private fun convertIntoS3Key(params: CompletedHelpPageParams): String {
        val sb = StringBuilder()
        sb.append(params.productName)
        sb.append('/')
        if (params.productVersion != "") {
            sb.append(params.productVersion)
            sb.append('/')
        }
        sb.append(params.pageName)
        return sb.toString()
    }
}