package tech.kekulta.data.services

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import tech.kekulta.data.db.dao.EventDao
import tech.kekulta.data.db.dao.UserDao
import tech.kekulta.domain.models.events.Event
import tech.kekulta.domain.models.events.EventId
import tech.kekulta.domain.models.users.UserId

class EventService(private val database: Database) {

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

    suspend fun createEvent(owner: UserId, name: String): Event? = dbQuery {
        try {
            UserDao.findById(owner.id)?.let { user ->
                EventDao.new {
                    this.owner = user.id
                    this.name = name
                }.toModel()
            }
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

    private fun EventDao.toModel() = Event(id = EventId(id.value), name = name, owner = owner?.let { UserId(it.value) })

    private suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(
        context = Dispatchers.IO, db = database,
    ) { block() }
}
