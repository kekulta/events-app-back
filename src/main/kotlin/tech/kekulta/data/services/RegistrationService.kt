package tech.kekulta.data.services

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import tech.kekulta.data.db.dao.*
import tech.kekulta.data.db.sheme.RegisterTokensTable
import tech.kekulta.data.db.sheme.UsersTable
import tech.kekulta.data.db.upsert
import tech.kekulta.domain.Constants
import tech.kekulta.domain.models.*

class RegistrationService(
    private val database: Database, private val userService: UserService,
) {

    suspend fun getRegistrationStatus(id: UserId): RegistrationStatus? = dbQuery {
        when {
            userService.getProfile(id) != null -> RegistrationStatus.REGISTERED
            getRegistrationToken(id) != null -> RegistrationStatus.NEED_REGISTRATION
            getVerificationCode(id) != null -> RegistrationStatus.CODE_SENT
            getUser(id) != null -> RegistrationStatus.CREATED
            else -> null
        }
    }

    suspend fun getRegistrationStatus(number: PhoneNumber): RegistrationStatus? = dbQuery {
        val user = UserDao.find { UsersTable.number eq number.number }.firstOrNull()

        user?.run {
            getRegistrationStatus(UserId(id.value))
        }
    }

    suspend fun getUser(id: UserId): User? = dbQuery {
        UserDao.findById(id.id)?.run { User(id = UserId(this.id.value), number = PhoneNumber(this.number)) }
    }

    suspend fun getVerificationCode(id: UserId): VerificationCodeValue? = dbQuery {
        VerificationCodeDao.findById(id.id)?.run { VerificationCodeValue(code = code) }
    }

    suspend fun getRegistrationToken(id: UserId): RegisterToken? = dbQuery {
        RegisterTokenDao.find { RegisterTokensTable.user eq id.id }.firstOrNull()?.run { RegisterToken(token) }
    }

    suspend fun startRegistration(phoneNumber: PhoneNumber): User? = dbQuery {
        try {
            if (getRegistrationStatus(phoneNumber) == null) {
                UserDao.new { number = phoneNumber.number }.run { User(id = UserId(id.value), number = phoneNumber) }
            } else {
                null
            }
        } catch (e: ExposedSQLException) {
            null
        }
    }

    suspend fun sendCode(id: UserId): Boolean = dbQuery {
        try {
            val user = UserDao.findById(id.id)

            val code = generateCode()
            user?.let {
                VerificationCodeDao.upsert(id.id) { this.code = code }
            } != null

        } catch (e: ExposedSQLException) {
            false
        }
    }

    suspend fun checkCode(id: UserId, code: VerificationCodeValue): TokenValue? = dbQuery {
        try {
            val codeDao = VerificationCodeDao.findById(id.id)
            val userDao = UserDao.findById(id.id)
            if (codeDao?.code == code.code && userDao != null) {
                codeDao.delete()

                when (getRegistrationStatus(id)) {
                    RegistrationStatus.REGISTERED -> AccessTokenDao.new {
                        user = userDao
                        token = generateToken()
                    }
                        .run { AccessToken(token) }

                    RegistrationStatus.CODE_SENT -> RegisterTokenDao.new {
                        user = userDao
                        token = generateToken()
                    }
                        .run { RegisterToken(token) }

                    else -> null
                }
            } else {
                null
            }
        } catch (e: ExposedSQLException) {
            null
        }
    }

    suspend fun finishRegistration(id: UserId, info: ProfileInfo): AccessToken? = dbQuery {
        try {
            val tokenDao = RegisterTokenDao.find { RegisterTokensTable.user eq id.id }.firstOrNull()

            if (tokenDao != null) {
                tokenDao.delete()

                ProfileDao.new(tokenDao.user.id.value) {
                    name = info.name
                    surname = info.surname
                    avatar = info.avatar.url
                }

                AccessTokenDao.new {
                    user = tokenDao.user
                    this.token = generateToken()
                }
                    .run { AccessToken(this.token) }

            } else {
                null
            }
        } catch (e: ExposedSQLException) {
            null
        }
    }

    suspend fun getAllVerificationCodes(): List<Pair<UserId, VerificationCodeValue>> = dbQuery {
        try {
            VerificationCodeDao.all().map { UserId(it.id.value) to VerificationCodeValue(it.code) }
        } catch (e: ExposedSQLException) {
            emptyList()
        }
    }

    suspend fun getAllAccessTokens(): List<Pair<UserId, AccessToken>> = dbQuery {
        try {
            AccessTokenDao.all().map { UserId(it.id.value) to AccessToken(it.token) }
        } catch (e: ExposedSQLException) {
            emptyList()
        }
    }

    suspend fun getAllRegisterTokens(): List<Pair<UserId, RegisterToken>> = dbQuery {
        try {
            RegisterTokenDao.all().map { UserId(it.id.value) to RegisterToken(it.token) }
        } catch (e: ExposedSQLException) {
            emptyList()
        }
    }

    private fun generateCode(length: Int = Constants.CODE_LEN): String {
        val allowedChars = ('0'..'9')
        return String(CharArray(length) { allowedChars.random() })
    }

    private fun generateToken(length: Int = Constants.TOKEN_LEN): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return String(CharArray(length) { allowedChars.random() })
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(
        context = Dispatchers.IO, db = database,
    ) { block() }
}