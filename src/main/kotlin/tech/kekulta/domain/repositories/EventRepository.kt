package tech.kekulta.domain.repositories

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.insert
import tech.kekulta.data.db.dao.EventDao
import tech.kekulta.data.db.dao.UserDao
import tech.kekulta.data.db.sheme.EventVisitorsTable
import tech.kekulta.domain.models.events.Event
import tech.kekulta.domain.models.events.EventId
import tech.kekulta.domain.models.users.UserId

interface EventRepository {
    suspend fun getAllEvents(): List<Event>

    suspend fun getEvent(id: EventId): Event?
    suspend fun getEvents(ids: List<EventId>): List<Event>
    suspend fun createEvent(owner: UserId, name: String): Event?
    suspend fun deleteEvent(id: EventId): Boolean

    suspend fun addVisitor(eventId: EventId, userId: UserId): Boolean
    suspend fun deleteVisitor(eventId: EventId, userId: UserId): Boolean
}