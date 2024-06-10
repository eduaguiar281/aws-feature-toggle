package io.arcotech.featuretoggles.filters

class GroupUserFeatureFilter (featureName: String, private val groupId: Int) : DefaultFeatureFilter(featureName, "") {

    private var configuration: GroupUserFeatureConfiguration? = null

    override fun evaluate(): Boolean {
        configuration = readConfiguration(GroupUserFeatureConfiguration::class.java)
        return configuration?.let { it.enabled && it.groupIds.contains(groupId) } ?: false
    }
}