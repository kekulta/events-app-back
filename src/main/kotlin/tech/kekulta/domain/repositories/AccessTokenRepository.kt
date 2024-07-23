package tech.kekulta.domain.repositories

import tech.kekulta.domain.models.users.AccessToken
import tech.kekulta.domain.models.users.UserId

interface AccessTokenRepository {
   suspend fun checkToken(token: AccessToken): UserId?
   suspend fun getAllTokens(): List<AccessToken>
}