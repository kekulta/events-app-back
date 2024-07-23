package tech.kekulta.data.repositories

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import tech.kekulta.data.db.dao.AccessTokenDao
import tech.kekulta.data.db.sheme.AccessTokensTable
import tech.kekulta.data.services.RegistrationService
import tech.kekulta.domain.models.AccessToken
import tech.kekulta.domain.models.UserId
import tech.kekulta.domain.repositories.AccessTokenRepository

class AccessTokenRepositoryImpl(
    private val database: Database,
    private val registrationService: RegistrationService,
) : AccessTokenRepository {
    override suspend fun checkToken(token: AccessToken): UserId? = dbQuery {
        AccessTokenDao.find { AccessTokensTable.token eq token.token }.firstOrNull()?.run { UserId(user.id.value) }
    }

    override suspend fun getAllTokens(): List<AccessToken> = registrationService.getAllAccessTokens().map { it.second }

    private suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(
        context = Dispatchers.IO, db = database,
    ) { block() }
}