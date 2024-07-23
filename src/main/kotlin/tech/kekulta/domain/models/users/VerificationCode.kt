package tech.kekulta.domain.models.users

import kotlinx.serialization.Serializable

@Serializable
data class VerificationCode(val id: UserId, val code: VerificationCodeValue)