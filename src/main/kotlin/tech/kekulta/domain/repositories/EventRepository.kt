package tech.kekulta.domain.repositories

import tech.kekulta.domain.models.events.Event
import tech.kekulta.domain.models.events.EventId
import tech.kekulta.domain.models.users.UserId

interface EventRepository {
    suspend fun getAllEvents(): List<Event>

    suspend fun getEvent(id: EventId): Event?
    suspend fun getEvents(ids: List<EventId>): List<Event>
    suspend fun createEvent(owner: UserId, name: String): Event?
    suspend fun deleteEvent(id: EventId): Boolean
}