package tech.kekulta.data.db.sheme

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import tech.kekulta.domain.Constants

object EventsTable : IntIdTable("events") {
    val name = varchar("name", Constants.EVENT_NAME_LEN)
    val owner = reference(
        "user_id", UsersTable,
        onDelete = ReferenceOption.SET_NULL,
        onUpdate = ReferenceOption.SET_NULL,
    ).nullable()
}
