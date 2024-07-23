package tech.kekulta.domain.models.events

import kotlinx.serialization.Serializable
import tech.kekulta.domain.models.communities.CommunityId
import tech.kekulta.domain.models.users.UserId

@Serializable
data class Event(
    val id: EventId,
    val owner: UserId?,
    val community: CommunityId?,
    val visitors: List<UserId>,
    val info: EventInfo
)