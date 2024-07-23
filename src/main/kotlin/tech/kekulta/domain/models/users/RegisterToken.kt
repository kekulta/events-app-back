package tech.kekulta.domain.models.users

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class RegisterToken(override val token: String) : TokenValue
