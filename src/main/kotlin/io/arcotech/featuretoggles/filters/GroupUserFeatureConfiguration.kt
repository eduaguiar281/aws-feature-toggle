package io.arcotech.featuretoggles.filters

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


class GroupUserFeatureConfiguration @JsonCreator constructor (@JsonProperty("enabled") enabled: Boolean = false,
                                                              @JsonProperty("grupoUsuarioIds") val groupIds: ArrayList<Int> = arrayListOf())
    : DefaultFeatureConfiguration(enabled) {
}