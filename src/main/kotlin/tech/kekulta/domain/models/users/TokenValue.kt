package tech.kekulta.domain.models.users

import kotlinx.serialization.Serializable

@Serializable
sealed interface TokenValue {
    val token: String
}