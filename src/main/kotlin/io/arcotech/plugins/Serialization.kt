package io.arcotech.plugins

import io.arcotech.featuretoggles.FeatureManagement
import io.arcotech.featuretoggles.filters.GroupUserFeatureConfiguration
import io.arcotech.featuretoggles.filters.GroupUserFeatureFilter
import io.arcotech.model.Customer
import io.arcotech.model.serialize
import io.arcotech.model.newSerialize
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSerialization(featureManagement: FeatureManagement) {
    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/json/kotlinx-serialization") {
                call.respond(mapOf("hello" to "world"))
            }

        get ("/api/customer"){
            val customer = Customer(1, "Eduardo", "Aguiar", "Eduardo Aguiar")
            if (featureManagement.isEnabled("executarNovoCodigo"))
                call.respond(customer.newSerialize())
            else
                call.respond(customer.serialize())
        }

        get ("/api/v2/customer"){
            val customer = Customer(2, "Lucas", "Ferronato", "Lucas Ferronato")
            val groupId = call.request.queryParameters["groupid"]?.toIntOrNull() ?: 0

            if (featureManagement.isEnabled( GroupUserFeatureFilter("featureDisponivel", groupId)))
                call.respond(customer.newSerialize())
            else
                call.respond(HttpStatusCode.NotFound)
        }

    }
}
