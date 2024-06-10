package io.arcotech.featuretoggles.filters

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

open class DefaultFeatureFilter constructor(
    private val featureName: String,
    private var configurationJson: String
) {

    private var configuration: DefaultFeatureConfiguration? = null

    open fun evaluate(): Boolean {
        configuration = readConfiguration(DefaultFeatureConfiguration::class.java)
        return configuration?.enabled ?: false
    }

    fun setConfigurationJson(json: String) {
        configurationJson = json
    }

    protected open fun <T : DefaultFeatureConfiguration> readConfiguration(featureClass: Class<T>): T? {
        val objectMapper = ObjectMapper().registerModule(JavaTimeModule())
        if (configurationJson.isBlank()) {
            return createDefaultInstance(featureClass)
        }
        return try {
            val root = objectMapper.readTree(configurationJson)
            val jsonNode = root.get(featureName)

            jsonNode?.let {
                try {
                    objectMapper.readValue(it.toString(), featureClass)
                } catch (ex: Exception) {
                    createDefaultInstance(featureClass)
                }
            } ?: createDefaultInstance(featureClass) // jsonNode é null
        } catch (ex: Exception) {
            createDefaultInstance(featureClass)
        }
    }

    private fun <T : DefaultFeatureConfiguration> createDefaultInstance(featureClass: Class<T>): T? {
        return try {
            featureClass.getDeclaredConstructor().newInstance()
        } catch (ex: Exception) {
            null
        }
    }
}