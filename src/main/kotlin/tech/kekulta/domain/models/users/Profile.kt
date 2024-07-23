package tech.kekulta.domain.models.users

import kotlinx.serialization.Serializable

@Serializable
data class Profile(val id: UserId, val number: PhoneNumber, val info: ProfileInfo)