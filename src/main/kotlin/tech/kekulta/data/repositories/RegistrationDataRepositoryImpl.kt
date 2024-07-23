package tech.kekulta.data.repositories

import tech.kekulta.data.services.RegistrationService
import tech.kekulta.domain.models.*
import tech.kekulta.domain.repositories.RegistrationDataRepository

class RegistrationDataRepositoryImpl(private val registrationService: RegistrationService) :
    RegistrationDataRepository {
    override suspend fun getUser(id: UserId): User? = registrationService.getUser(id)

    override suspend fun getVerificationCode(id: UserId): VerificationCodeValue? =
        registrationService.getVerificationCode(id)

    override suspend fun getRegistrationToken(id: UserId): RegisterToken? = registrationService.getRegistrationToken(id)

    override suspend fun getAllVerificationCodes(): List<VerificationCode> =
        registrationService.getAllVerificationCodes().map { (id, value) -> VerificationCode(id, value) }

    override suspend fun getAllRegisterTokens(): List<Token> =
        registrationService.getAllRegisterTokens().map { (id, value) -> Token(id, value) }

    override suspend fun getAllAccessTokens(): List<Token> =
        registrationService.getAllAccessTokens().map { (id, value) -> Token(id, value) }
}