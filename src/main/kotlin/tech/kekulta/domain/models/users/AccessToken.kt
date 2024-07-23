package tech.kekulta.domain.models.users

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class AccessToken(override val token: String) : TokenValue