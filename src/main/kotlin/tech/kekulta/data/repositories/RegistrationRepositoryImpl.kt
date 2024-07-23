package tech.kekulta.data.repositories

import tech.kekulta.data.services.RegistrationService
import tech.kekulta.domain.models.*
import tech.kekulta.domain.repositories.RegistrationRepository

class RegistrationRepositoryImpl(
    private val registrationService: RegistrationService,
) : RegistrationRepository {
    override suspend fun getRegistrationStatus(id: UserId): RegistrationStatus? =
        registrationService.getRegistrationStatus(id)

    override suspend fun getRegistrationStatus(number: PhoneNumber): RegistrationStatus? =
        registrationService.getRegistrationStatus(number)

    override suspend fun startRegistration(number: PhoneNumber): User? = registrationService.startRegistration(number)

    override suspend fun sendCode(id: UserId): Boolean = registrationService.sendCode(id)

    override suspend fun checkCode(id: UserId, code: VerificationCodeValue): TokenValue? = registrationService.checkCode(id, code)

    override suspend fun finishRegistration(id: UserId, info: ProfileInfo): AccessToken? =
        registrationService.finishRegistration(id, info)
}

