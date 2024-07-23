package tech.kekulta.data.db.sheme

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import tech.kekulta.domain.Constants

abstract class TokensTable(name: String) : IntIdTable(name = name) {
    val user = reference(
        "user_id", UsersTable,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE,
    )
    var token = varchar("token", length = Constants.TOKEN_LEN).uniqueIndex()
}


