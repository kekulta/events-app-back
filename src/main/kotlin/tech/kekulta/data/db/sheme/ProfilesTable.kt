package tech.kekulta.data.db.sheme


import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption
import tech.kekulta.domain.Constants

object ProfilesTable : IdTable<Int>("user_profiles") {
    override val id = reference(
        "id",
        UsersTable,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE,
    )
    val name = varchar("name", length = Constants.NAME_LEN)
    val surname = varchar("surname", length = Constants.NAME_LEN)
    val avatar = varchar("avatar", length = Constants.AVATAR_LEN).nullable()

    override val primaryKey = PrimaryKey(id)
}

