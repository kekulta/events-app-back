package tech.kekulta.domain.models.communities

import kotlinx.serialization.Serializable
import tech.kekulta.domain.models.events.EventId
import tech.kekulta.domain.models.users.UserId

@Serializable
data class Community(val id: CommunityId, val members: List<UserId>, val events: List<EventId>)