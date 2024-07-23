package tech.kekulta.domain.models

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Avatar(val url: String?)
