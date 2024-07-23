package tech.kekulta.domain.repositories

import tech.kekulta.domain.models.users.*

interface RegistrationDataRepository {
    suspend fun getUser(id: UserId): User?
    suspend fun getVerificationCode(id: UserId): VerificationCodeValue?
    suspend fun getRegistrationToken(id: UserId): RegisterToken?

    suspend fun getAllVerificationCodes(): List<VerificationCode>
    suspend fun getAllRegisterTokens(): List<Token>
    suspend fun getAllAccessTokens(): List<Token>
}