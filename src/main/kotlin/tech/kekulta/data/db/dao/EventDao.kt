package tech.kekulta.data.db.dao

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import tech.kekulta.data.db.sheme.EventsTable
import tech.kekulta.data.db.sheme.ProfilesTable

class EventDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventDao>(EventsTable)

    var name by EventsTable.name
    var owner by EventsTable.owner
}
