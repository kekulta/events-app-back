package tech.kekulta.domain.models.communities

import kotlinx.serialization.Serializable
import tech.kekulta.domain.models.users.Avatar

@Serializable
data class CommunityInfo(val name: String, val description: String?, val avatar: Avatar)