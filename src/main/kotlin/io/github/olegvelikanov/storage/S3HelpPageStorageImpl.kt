package io.github.olegvelikanov.storage

import io.github.olegvelikanov.config.S3Config
import io.github.olegvelikanov.domain.CompletedHelpPageParams
import io.github.olegvelikanov.domain.HelpPage
import software.amazon.awssdk.core.sync.ResponseTransformer
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.net.URI

class S3HelpPageStorageImpl(config: S3Config) : HelpPageStorage {

    private val s3BucketName: String
    private val s3Client: S3Client

    init {
        s3BucketName = config.bucket
        val s3Region = Region.of(config.region);
        s3Client = S3Client.builder()
            .region(s3Region)
            .endpointOverride(URI(config.endpoint))
            .build()
    }

    override fun getHelpPageFor(params: CompletedHelpPageParams): HelpPage {
        val getRequest = GetObjectRequest.builder()
            .bucket(s3BucketName)
            .key(convertIntoS3Key(params))
            .build()
        val responseBytes = s3Client.getObject(getRequest, ResponseTransformer.toBytes())
        return HelpPage(responseBytes.asByteArray())
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