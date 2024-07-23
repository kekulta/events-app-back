package tech.kekulta.domain.models.users

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Avatar(val url: String?)
