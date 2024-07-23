package tech.kekulta.domain.repositories

import tech.kekulta.domain.models.events.Event
import tech.kekulta.domain.models.events.EventId
import tech.kekulta.domain.models.events.EventInfo
import tech.kekulta.domain.models.users.UserId

interface EventRepository {
    suspend fun getAllEvents(): List<Event>

    suspend fun getEvent(id: EventId): Event?
    suspend fun getEvents(ids: List<EventId>): List<Event>
    suspend fun createEvent(owner: UserId, info: EventInfo): Event?
    suspend fun deleteEvent(id: EventId): Boolean
    suspend fun updateEvent(id: EventId, info: EventInfo): Event?

    suspend fun addVisitor(eventId: EventId, userId: UserId): Boolean
    suspend fun deleteVisitor(eventId: EventId, userId: UserId): Boolean
}