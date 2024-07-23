package tech.kekulta.data.db.sheme

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object CommunityMembersTable : Table() {
    val community = reference(
        "community", CommunitiesTable, onUpdate = ReferenceOption.CASCADE, onDelete = ReferenceOption.CASCADE,
    )
    val user = reference(
        "user", UsersTable, onUpdate = ReferenceOption.CASCADE, onDelete = ReferenceOption.CASCADE,
    )
    override val primaryKey = PrimaryKey(
        community,
        user,
    )
}
