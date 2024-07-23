package tech.kekulta.domain.models

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class VerificationCodeValue(val code: String)