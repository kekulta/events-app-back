package tech.kekulta.data.db.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import tech.kekulta.data.db.dao.UserDao
import tech.kekulta.data.db.sheme.TokensTable

abstract class TokenEntity(id: EntityID<Int>, table: TokensTable) : IntEntity(id) {
    var user by UserDao referencedOn table.user
    var token by table.token
}
