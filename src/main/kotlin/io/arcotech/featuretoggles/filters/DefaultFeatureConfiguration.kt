package io.arcotech.featuretoggles.filters

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

open class DefaultFeatureConfiguration @JsonCreator constructor (
    @JsonProperty("enabled") val enabled: Boolean = false)