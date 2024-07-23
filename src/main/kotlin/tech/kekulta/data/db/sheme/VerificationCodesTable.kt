package tech.kekulta.data.db.sheme

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption
import tech.kekulta.domain.Constants

object VerificationCodesTable : IdTable<Int>(name = "verification_table") {
    override val id = reference(
        "id", UsersTable, onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE,
    )
    val code = varchar("code", Constants.CODE_LEN)
}
