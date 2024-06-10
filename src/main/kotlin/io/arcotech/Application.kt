package io.arcotech

import io.arcotech.featuretoggles.FeatureManagement
import io.arcotech.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val service = AwsService(environment)
    val cache = CacheService()
    val featureManagement = FeatureManagement(service, cache)
    configureSerialization(featureManagement)
    configureRouting()
}
