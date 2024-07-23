package tech.kekulta.data.db.sheme


import org.jetbrains.exposed.dao.id.IntIdTable
import tech.kekulta.domain.Constants

object UsersTable : IntIdTable("users") {
    val number = varchar("number", length = Constants.NUMBER_LEN).uniqueIndex()
}
