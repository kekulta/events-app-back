package tech.kekulta.data.repositories

import tech.kekulta.data.services.UserService
import tech.kekulta.domain.models.*
import tech.kekulta.domain.repositories.UserRepository

class UserRepositoryImpl(private val userService: UserService) : UserRepository {
    override suspend fun updateProfile(id: UserId, info: ProfileInfo): Profile? = userService.updateProfile(id, info)

    override suspend fun getProfile(id: UserId): Profile? = userService.getProfile(id)

    override suspend fun getProfiles(ids: List<UserId>): List<Profile> = userService.getProfiles(ids)

    override suspend fun getAllProfiles(): List<Profile> = userService.getAllProfiles()

    override suspend fun deleteUser(id: UserId): Boolean = userService.deleteUser(id)

    override suspend fun getUser(id: UserId): User? = userService.getUser(id)

    override suspend fun getUser(number: PhoneNumber): User? = userService.getUser(number)

    override suspend fun getUsers(ids: List<UserId>): List<User> = userService.getUsers(ids)

    override suspend fun getAllUsers(): List<User> = userService.getAllUsers()
}

