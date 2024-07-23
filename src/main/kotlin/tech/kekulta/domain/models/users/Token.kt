package tech.kekulta.domain.models.users

import kotlinx.serialization.Serializable

@Serializable
data class Token(val id: UserId, val token: TokenValue)