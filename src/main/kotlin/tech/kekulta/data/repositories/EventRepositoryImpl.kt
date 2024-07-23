package tech.kekulta.data.repositories

import tech.kekulta.data.services.EventService
import tech.kekulta.domain.models.communities.CommunityId
import tech.kekulta.domain.models.events.Event
import tech.kekulta.domain.models.events.EventId
import tech.kekulta.domain.models.events.EventInfo
import tech.kekulta.domain.models.users.UserId
import tech.kekulta.domain.repositories.EventRepository

class EventRepositoryImpl(
    private val eventService: EventService,
) : EventRepository {
    override suspend fun getAllEvents(): List<Event> = eventService.getAllEvents()

    override suspend fun getEvent(id: EventId): Event? = eventService.getEvent(id)

    override suspend fun getEvents(ids: List<EventId>): List<Event> = eventService.getEvents(ids)

    override suspend fun createEvent(owner: UserId, community: CommunityId?, info: EventInfo): Event? =
        eventService.createEvent(owner, community, info)

    override suspend fun updateEvent(id: EventId, info: EventInfo): Event? = eventService.updateEvent(id, info)

    override suspend fun updateCommunity(eventId: EventId, communityId: CommunityId): Event? =
        eventService.updateCommunity(eventId, communityId)

    override suspend fun deleteEvent(id: EventId): Boolean = eventService.deleteEvent(id)

    override suspend fun addVisitor(eventId: EventId, userId: UserId): Boolean =
        eventService.addVisitor(eventId, userId)

    override suspend fun deleteVisitor(eventId: EventId, userId: UserId): Boolean =
        eventService.deleteVisitor(eventId, userId)
}