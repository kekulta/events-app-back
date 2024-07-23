package tech.kekulta.domain.models

import kotlinx.serialization.Serializable

@Serializable
sealed interface TokenValue {
    val token: String
}