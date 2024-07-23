package tech.kekulta.domain.repositories

import tech.kekulta.domain.models.users.*

interface UserRepository {
    suspend fun updateProfile(id: UserId, info: ProfileInfo): Profile?
    suspend fun getProfile(id: UserId): Profile?
    suspend fun getProfiles(ids: List<UserId>): List<Profile>
    suspend fun getAllProfiles(): List<Profile>

    suspend fun deleteUser(id: UserId): Boolean
    suspend fun getUser(number: PhoneNumber): User?
    suspend fun getUser(id: UserId): User?
    suspend fun getUsers(ids: List<UserId>): List<User>
    suspend fun getAllUsers(): List<User>
}