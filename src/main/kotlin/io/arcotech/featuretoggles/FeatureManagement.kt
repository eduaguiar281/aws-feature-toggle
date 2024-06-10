package io.arcotech.Features

import io.arcotech.AwsService
import io.arcotech.CacheService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FeatureManagement (private val service: AwsService, private val cacheService: CacheService) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val key: String = service.getKey()
    private var featureConfiguration: String? = null

    suspend fun getFeatureToggles() {
        val cachedToggles = cacheService.getValue(key)
        if (cachedToggles != null) {
            // Serve stale data immediately
            scope.launch {
                try {
                    // Revalidate in background
                    val freshToggles = service.fetchFeatureToggles()
                    cacheService.saveValue(freshToggles, key)
                } catch (e: Exception) {
                    println("Ocorreu um erro ao recuperar a configuração. Mensagem: ${e.message}")
                }
            }
            featureConfiguration = cachedToggles
        } else {
            val freshToggles = service.fetchFeatureToggles()
            cacheService.saveValue(freshToggles, key)
            featureConfiguration = freshToggles
        }
    }
}