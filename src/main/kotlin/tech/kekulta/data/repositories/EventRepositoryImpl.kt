package tech.kekulta.data.repositories

import tech.kekulta.data.services.EventService
import tech.kekulta.domain.models.events.Event
import tech.kekulta.domain.models.events.EventId
import tech.kekulta.domain.models.users.UserId
import tech.kekulta.domain.repositories.EventRepository

class EventRepositoryImpl(
    private val eventService: EventService,
) : EventRepository {
    override suspend fun getAllEvents(): List<Event> = eventService.getAllEvents()

    override suspend fun getEvent(id: EventId): Event? = eventService.getEvent(id)

    override suspend fun getEvents(ids: List<EventId>): List<Event> = eventService.getEvents(ids)

    override suspend fun createEvent(owner: UserId, name: String): Event? = eventService.createEvent(owner, name)

    override suspend fun deleteEvent(id: EventId): Boolean = eventService.deleteEvent(id)
}