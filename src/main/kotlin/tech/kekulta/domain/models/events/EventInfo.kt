package tech.kekulta.domain.models.events

import kotlinx.serialization.Serializable
import tech.kekulta.domain.models.users.Avatar

@Serializable
data class EventInfo(val name: String, val description: String?, val avatar: Avatar)