package tech.kekulta.data.services

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import tech.kekulta.data.db.dao.EventDao
import tech.kekulta.data.db.dao.UserDao
import tech.kekulta.data.db.sheme.EventVisitorsTable
import tech.kekulta.domain.models.events.Event
import tech.kekulta.domain.models.events.EventId
import tech.kekulta.domain.models.events.EventInfo
import tech.kekulta.domain.models.users.Avatar
import tech.kekulta.domain.models.users.UserId

class EventService(private val database: Database) {

    suspend fun addVisitor(eventId: EventId, userId: UserId): Boolean = dbQuery {
        try {
            UserDao.findById(userId.id)?.let { user ->
                EventDao.findById(eventId.id)?.let { event ->
                    EventVisitorsTable.insert {
                        it[this.user] = user.id
                        it[this.event] = event.id
                    }.resultedValues?.size
                }
            } != null
        } catch (e: ExposedSQLException) {
            false
        }
    }

    suspend fun deleteVisitor(eventId: EventId, userId: UserId): Boolean = dbQuery {
        try {
            UserDao.findById(userId.id)?.let { user ->
                EventDao.findById(eventId.id)?.let { event ->
                    EventVisitorsTable.deleteWhere {
                        (EventVisitorsTable.user eq user.id) and (EventVisitorsTable.event eq event.id)
                    }
                }
            }?.let { it > 0 } ?: false
        } catch (e: ExposedSQLException) {
            false
        }
    }

    suspend fun getAllEvents(): List<Event> = dbQuery {
        EventDao.all().map { it.toModel() }
    }

    suspend fun getEvent(id: EventId): Event? = dbQuery {
        EventDao.findById(id.id)?.toModel()
    }

    suspend fun getEvents(ids: List<EventId>): List<Event> = dbQuery {
        ids.mapNotNull { id ->
            EventDao.findById(id.id)?.toModel()
        }
    }

    suspend fun createEvent(owner: UserId, info: EventInfo): Event? = dbQuery {
        try {
            UserDao.findById(owner.id)?.let { user ->
                EventDao.new {
                    this.owner = user.id
                    this.name = info.name
                    this.avatar = info.avatar.url
                    this.description = info.description
                }.toModel()
            }
        } catch (e: ExposedSQLException) {
            null
        }
    }

    suspend fun updateEvent(id: EventId, info: EventInfo): Event? = dbQuery {
        try {
            EventDao.findById(id.id)?.apply {
                this.name = info.name
                this.avatar = info.avatar.url
                this.description = info.description
            }?.toModel()
        } catch (e: ExposedSQLException) {
            null
        }
    }

    suspend fun deleteEvent(id: EventId): Boolean = dbQuery {
        try {
            EventDao.findById(id.id)?.delete() != null
        } catch (e: ExposedSQLException) {
            false
        }
    }

    private fun EventDao.toModel() = Event(
        id = EventId(id.value),
        owner = owner?.let { UserId(it.value) },
        visitors = visitors.map { dao -> UserId(dao.id.value) },
        info = EventInfo(
            name = name, description = description, avatar = Avatar(avatar),
        )
    )

    private suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(
        context = Dispatchers.IO, db = database,
    ) { block() }
}
