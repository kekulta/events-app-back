package tech.kekulta.data.services

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import tech.kekulta.data.db.dao.ProfileDao
import tech.kekulta.data.db.dao.UserDao
import tech.kekulta.data.db.sheme.UsersTable
import tech.kekulta.data.db.upsert
import tech.kekulta.domain.models.users.*

class UserService(private val database: Database) {
    suspend fun updateProfile(id: UserId, info: ProfileInfo): Profile? = dbQuery {
        UserDao.findById(id.id)?.number?.let { number ->
            ProfileDao.upsert(id.id) {
                name = info.name
                surname = info.surname
                avatar = info.avatar.url
            }.run {
                Profile(
                    id = id,
                    number = PhoneNumber(number),
                    info = ProfileInfo(name = name, surname = surname, avatar = Avatar(avatar)),
                )
            }
        }
    }

    suspend fun getProfile(id: UserId): Profile? = dbQuery {
        UserDao.findById(id.id)?.number?.let { number ->
            ProfileDao.findById(id.id)?.run {
                Profile(
                    id = id,
                    number = PhoneNumber(number),
                    info = ProfileInfo(name = name, surname = surname, avatar = Avatar(avatar)),
                )
            }
        }
    }

    suspend fun getProfiles(ids: List<UserId>): List<Profile> = dbQuery {
        ids.mapNotNull { id ->
            UserDao.findById(id.id)?.number?.let { number ->
                ProfileDao.findById(id.id)?.run {
                    Profile(
                        id = id,
                        number = PhoneNumber(number),
                        info = ProfileInfo(name = name, surname = surname, avatar = Avatar(avatar)),
                    )
                }
            }
        }
    }

    suspend fun getAllProfiles(): List<Profile> = dbQuery {
        UserDao.all().mapNotNull { user ->
            user.number.let { number ->
                ProfileDao.findById(user.id)?.run {
                    Profile(
                        id = UserId(user.id.value),
                        number = PhoneNumber(number),
                        info = ProfileInfo(name = name, surname = surname, avatar = Avatar(avatar)),
                    )
                }
            }
        }
    }

    suspend fun deleteUser(id: UserId): Boolean = dbQuery {
        UserDao.findById(id.id)?.delete() != null
    }

    suspend fun getUser(number: PhoneNumber): User? = dbQuery {
        UserDao.find { UsersTable.number eq number.number }.firstOrNull()
            ?.run { User(UserId(this.id.value), PhoneNumber(this.number)) }
    }

    suspend fun getUser(id: UserId): User? = dbQuery {
        UserDao.findById(id.id)?.run { User(UserId(this.id.value), PhoneNumber(number)) }
    }

    suspend fun getUsers(ids: List<UserId>): List<User> = dbQuery {
        ids.mapNotNull { id ->
            UserDao.findById(id.id)?.run { User(UserId(this.id.value), PhoneNumber(number)) }
        }
    }

    suspend fun getAllUsers(): List<User> = dbQuery {
        UserDao.all().map { user -> User(id = UserId(user.id.value), number = PhoneNumber(user.number)) }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(
        context = Dispatchers.IO, db = database,
    ) { block() }
}