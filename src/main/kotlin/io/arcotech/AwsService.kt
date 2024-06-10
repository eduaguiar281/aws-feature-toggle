package io.arcotech

import io.ktor.server.application.*
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationRequest
import software.amazon.awssdk.services.appconfigdata.model.StartConfigurationSessionRequest

class AwsService (environment: ApplicationEnvironment) {
    private val client = AppConfigDataClient.builder()
        .region(Region.US_EAST_2)
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build()

    private val applicationId = environment.config.propertyOrNull("feature-toggles.applicationId")?.getString() ?: throw IllegalArgumentException()
    private val environmentId = environment.config.propertyOrNull("feature-toggles.environmentId")?.getString() ?: throw IllegalArgumentException()
    private val configurationProfileId = environment.config.propertyOrNull("feature-toggles.profileId")?.getString() ?: throw IllegalArgumentException()

    fun getKey():String{
        return "$applicationId-$configurationProfileId-$environmentId"
    }
    suspend fun fetchFeatureToggles(): String {
        val sessionResponse = client.startConfigurationSession(
            StartConfigurationSessionRequest.builder()
                .applicationIdentifier(applicationId)
                .environmentIdentifier(environmentId)
                .configurationProfileIdentifier(configurationProfileId)
                .build()
        )

        val configurationToken = sessionResponse.initialConfigurationToken()

        val configResponse = client.getLatestConfiguration(
            GetLatestConfigurationRequest.builder()
                .configurationToken(configurationToken)
                .build()
        )

        return configResponse.configuration().asUtf8String()
    }
}
