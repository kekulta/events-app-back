package tech.kekulta.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: UserId, val number: PhoneNumber)