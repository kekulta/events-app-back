package tech.kekulta.domain.models.events

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class EventId(val id: Int)