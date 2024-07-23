package tech.kekulta.domain.models

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class PhoneNumber(val number: String)