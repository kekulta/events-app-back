package tech.kekulta.data.repositories

import tech.kekulta.data.services.CommunityService
import tech.kekulta.domain.models.communities.Community
import tech.kekulta.domain.models.communities.CommunityId
import tech.kekulta.domain.models.communities.CommunityInfo
import tech.kekulta.domain.models.users.UserId
import tech.kekulta.domain.repositories.CommunityRepository

class CommunityRepositoryImpl(
    private val communityService: CommunityService,
) : CommunityRepository {
    override suspend fun getAllCommunities(): List<Community> = communityService.getAllCommunities()

    override suspend fun getCommunity(id: CommunityId): Community? = communityService.getCommunity(id)

    override suspend fun getCommunities(ids: List<CommunityId>): List<Community> = communityService.getCommunities(ids)

    override suspend fun createCommunity(owner: UserId, info: CommunityInfo): Community? =
        communityService.createCommunity(owner, info)

    override suspend fun deleteCommunity(id: CommunityId): Boolean = communityService.deleteCommunity(id)

    override suspend fun updateCommunity(id: CommunityId, info: CommunityInfo): Community? =
        communityService.updateCommunity(id, info)

    override suspend fun addMember(eventId: CommunityId, userId: UserId): Boolean =
        communityService.addMember(eventId, userId)

    override suspend fun deleteMember(eventId: CommunityId, userId: UserId): Boolean =
        communityService.deleteMember(eventId, userId)
}