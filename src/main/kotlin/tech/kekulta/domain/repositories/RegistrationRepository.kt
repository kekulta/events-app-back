package tech.kekulta.domain.repositories

import tech.kekulta.domain.models.*

interface RegistrationRepository {
    suspend fun getRegistrationStatus(id: UserId): RegistrationStatus?
    suspend fun getRegistrationStatus(number: PhoneNumber): RegistrationStatus?

    suspend fun startRegistration(number: PhoneNumber): User?
    suspend fun sendCode(id: UserId): Boolean
    suspend fun checkCode(id: UserId, code: VerificationCodeValue): TokenValue?
    suspend fun finishRegistration(id: UserId, info: ProfileInfo): AccessToken?
}

