package tech.kekulta.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Token(val id: UserId, val token: TokenValue)