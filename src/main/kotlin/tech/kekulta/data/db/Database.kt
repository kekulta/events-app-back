package tech.kekulta.data.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import tech.kekulta.data.db.sheme.*

val AllTables = listOf(
    AccessTokensTable,
    ProfilesTable,
    UsersTable,
    RegisterTokensTable,
    VerificationCodesTable,
    EventsTable,
)

fun createDatabase(): Database {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", user = "root", driver = "org.h2.Driver", password = ""
    )

    transaction(database) {
        SchemaUtils.create(*AllTables.toTypedArray())
    }

    return database
}