package tech.kekulta.domain.repositories

import tech.kekulta.domain.models.RegisterToken
import tech.kekulta.domain.models.UserId

interface RegisterTokenRepository {
    suspend fun checkToken(token: RegisterToken): UserId?
    suspend fun getAllTokens(): List<RegisterToken>
}

