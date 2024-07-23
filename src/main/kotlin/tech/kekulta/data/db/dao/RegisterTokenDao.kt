package tech.kekulta.data.db.dao

import org.jetbrains.exposed.dao.id.EntityID
import tech.kekulta.data.db.entity.TokenEntity
import tech.kekulta.data.db.entity.TokenEntityClass
import tech.kekulta.data.db.sheme.RegisterTokensTable


class RegisterTokenDao(id: EntityID<Int>) : TokenEntity(id, RegisterTokensTable) {
    companion object : TokenEntityClass(RegisterTokensTable)
}
