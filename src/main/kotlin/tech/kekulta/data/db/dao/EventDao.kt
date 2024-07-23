package tech.kekulta.data.db.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import tech.kekulta.data.db.sheme.EventVisitorsTable
import tech.kekulta.data.db.sheme.EventsTable

class EventDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventDao>(EventsTable)

    var name by EventsTable.name
    var owner by EventsTable.owner
    var community by EventsTable.community
    var description by EventsTable.description
    var avatar by EventsTable.avatar
    val visitors by UserDao via EventVisitorsTable
}
