package tech.kekulta.data.db.dao

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import tech.kekulta.data.db.sheme.ProfilesTable

class ProfileDao(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, ProfileDao>(ProfilesTable)

    var name by ProfilesTable.name
    var surname by ProfilesTable.surname
    var avatar by ProfilesTable.avatar
}
