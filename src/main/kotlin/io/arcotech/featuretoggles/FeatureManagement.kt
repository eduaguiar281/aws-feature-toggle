package io.arcotech.featuretoggles

import io.arcotech.AwsService
import io.arcotech.CacheService
import io.arcotech.featuretoggles.filters.DefaultFeatureFilter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class FeatureManagement (private val service: AwsService, private val cacheService: CacheService) {

    private val key: String = service.getKey()
    private val refreshThreshold: Long = 1.minutes.inWholeMilliseconds // Atualizar se o cache estiver prestes a expirar em 1 minuto

    private suspend fun getFeatureToggles(): String {
        val cachedEntry = cacheService.getValue(key)
        if (cachedEntry != null) {
            val currentTime = System.currentTimeMillis()
            // Checar se está prestes a expirar
            if (currentTime - cachedEntry.timestamp >= (2.minutes.inWholeMilliseconds - refreshThreshold)) {
                // Lançar atualização em segundo plano
                GlobalScope.launch {
                    refreshFeatureToggle()
                }
            }
            return cachedEntry.value
        }
        return refreshFeatureToggle()
    }

    private suspend fun refreshFeatureToggle(): String{
        val freshToggles = service.fetchFeatureToggles()
        cacheService.saveValue(freshToggles, key)
        return freshToggles
    }

    suspend fun isEnabled(featureName: String):Boolean{
        val filter = DefaultFeatureFilter(featureName, getFeatureToggles())
        return filter.evaluate()
    }

    suspend fun <T: DefaultFeatureFilter> isEnabled(filter: T): Boolean{
        filter.setConfigurationJson(getFeatureToggles())
        return filter.evaluate()
    }
}