package tech.kekulta.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ProfileInfo(val name: String, val surname: String, val avatar: Avatar)