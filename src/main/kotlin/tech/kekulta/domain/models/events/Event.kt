package tech.kekulta.domain.models.events

import kotlinx.serialization.Serializable
import tech.kekulta.domain.models.users.UserId

@Serializable
data class Event(val id: EventId, val name: String, val owner: UserId?, val visitors: List<UserId>)