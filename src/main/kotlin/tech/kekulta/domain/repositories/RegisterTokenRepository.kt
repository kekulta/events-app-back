package tech.kekulta.domain.repositories

import tech.kekulta.domain.models.users.RegisterToken
import tech.kekulta.domain.models.users.UserId

interface RegisterTokenRepository {
    suspend fun checkToken(token: RegisterToken): UserId?
    suspend fun getAllTokens(): List<RegisterToken>
}

