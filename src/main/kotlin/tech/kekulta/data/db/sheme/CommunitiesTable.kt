package tech.kekulta.data.db.sheme

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import tech.kekulta.domain.Constants

object CommunitiesTable : IntIdTable("communities") {
    val name = varchar("name", Constants.EVENT_NAME_LEN)
    val avatar = varchar("avatar", Constants.AVATAR_LEN).nullable()
    val description = varchar("description", Constants.EVENT_DESCRIPTION_LEN).nullable()
    val owner = reference(
        "user_id", UsersTable,
        onDelete = ReferenceOption.SET_NULL,
        onUpdate = ReferenceOption.SET_NULL,
    ).nullable()
}
