package tech.kekulta.domain.repositories

import tech.kekulta.domain.models.AccessToken
import tech.kekulta.domain.models.UserId

interface AccessTokenRepository {
   suspend fun checkToken(token: AccessToken): UserId?
   suspend fun getAllTokens(): List<AccessToken>
}