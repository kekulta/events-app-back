package tech.kekulta.data.repositories

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import tech.kekulta.data.db.dao.RegisterTokenDao
import tech.kekulta.data.db.sheme.RegisterTokensTable
import tech.kekulta.data.services.RegistrationService
import tech.kekulta.domain.models.RegisterToken
import tech.kekulta.domain.models.UserId
import tech.kekulta.domain.repositories.RegisterTokenRepository

class RegisterTokenRepositoryImpl(
    private val database: Database,
    private val registrationService: RegistrationService,
) : RegisterTokenRepository {
    override suspend fun checkToken(token: RegisterToken): UserId? = dbQuery {
        RegisterTokenDao.find { RegisterTokensTable.token eq token.token }.firstOrNull()?.run { UserId(user.id.value) }
    }

    override suspend fun getAllTokens(): List<RegisterToken> =
        registrationService.getAllRegisterTokens().map { it.second }

    private suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(
        context = Dispatchers.IO, db = database,
    ) { block() }
}