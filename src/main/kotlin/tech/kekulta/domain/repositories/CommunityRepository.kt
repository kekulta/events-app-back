package tech.kekulta.domain.repositories

import tech.kekulta.domain.models.communities.Community
import tech.kekulta.domain.models.communities.CommunityId
import tech.kekulta.domain.models.communities.CommunityInfo
import tech.kekulta.domain.models.users.UserId

interface CommunityRepository {
    suspend fun getAllCommunities(): List<Community>

    suspend fun getCommunity(id: CommunityId): Community?
    suspend fun getCommunities(ids: List<CommunityId>): List<Community>
    suspend fun createCommunity(owner: UserId, info: CommunityInfo): Community?
    suspend fun deleteCommunity(id: CommunityId): Boolean
    suspend fun updateCommunity(id: CommunityId, info: CommunityInfo): Community?

    suspend fun addMember(eventId: CommunityId, userId: UserId): Boolean
    suspend fun deleteMember(eventId: CommunityId, userId: UserId): Boolean
}