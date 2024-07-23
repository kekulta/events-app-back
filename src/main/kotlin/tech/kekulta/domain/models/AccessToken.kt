package tech.kekulta.domain.models

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class AccessToken(override val token: String) : TokenValue