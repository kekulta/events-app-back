package tech.kekulta.plugins

import org.jetbrains.exposed.sql.Database


class UsersService(private val database: Database) {

//    init {
//        transaction(database) {
//            SchemaUtils.create(Users)
//        }
//    }
//
//    private fun User.toModel() =
//        EventsProfile(id = UserId(id.value.toString()), name = name, surname = surname, avatar = Avatar(avatar))
//
//    private fun rowToModel(row: ResultRow) = EventsProfile(
//        id = UserId(row[UsersTable.id].value.toString()),
//        name = row[UsersTable.name],
//        surname = row[UsersTable.surname],
//        avatar = Avatar(
//            row[UsersTable.avatar]
//        ),
//    )
//
//    private suspend fun <T> dbQuery(block: suspend () -> T): T =
//        newSuspendedTransaction(
//            context = Dispatchers.IO, db = database,
//        ) { block() }
//
//    suspend fun create(user: EventsProfileInfo): EventsProfile = dbQuery {
//        UserDao.new {
//            name = user.name
//            surname = user.surname
//            avatar = user.avatar.url
//        }.daoToModel()
//    }
//
//    suspend fun read(id: UserId): EventsProfile? = dbQuery {
//        UsersTable.select { UsersTable.id eq id.id.toIntOrNull() }.map(::rowToModel).singleOrNull()
//    }
//
//    suspend fun read(ids: List<UserId>): List<EventsProfile> = dbQuery {
//        val intIds = ids.mapNotNull { it.id.toIntOrNull() }
//        UsersTable.select { UsersTable.id inList intIds }.map(::rowToModel)
//    }
//
//    suspend fun readAll(): List<EventsProfile> = dbQuery {
//        UsersTable.selectAll().map(::rowToModel)
//    }
//
//    suspend fun update(id: UserId, user: EventsProfileInfo): EventsProfile? = dbQuery {
//        UsersTable.update({ UsersTable.id eq id.id.toIntOrNull() }) { row ->
//            row[name] = user.name
//            row[surname] = user.surname
//            row[avatar] = user.avatar.url
//        }
//
//        read(id)
//    }
//
//    suspend fun delete(id: UserId): Boolean = dbQuery {
//        UsersTable.deleteWhere { UsersTable.id eq id.id.toIntOrNull() } != 0
//    }
}