package tech.kekulta.data.db.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import tech.kekulta.data.db.sheme.CommunitiesTable
import tech.kekulta.data.db.sheme.CommunityMembersTable
import tech.kekulta.data.db.sheme.EventsTable

class CommunityDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CommunityDao>(CommunitiesTable)

    var name by CommunitiesTable.name
    var owner by CommunitiesTable.owner
    var description by CommunitiesTable.description
    var avatar by CommunitiesTable.avatar
    val members by UserDao via CommunityMembersTable
    val events by EventDao optionalReferrersOn EventsTable.community
}
