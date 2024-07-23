package tech.kekulta.domain.models.users

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: UserId, val number: PhoneNumber)