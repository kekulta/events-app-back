package tech.kekulta.data.services

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import tech.kekulta.data.db.dao.CommunityDao
import tech.kekulta.data.db.dao.UserDao
import tech.kekulta.data.db.sheme.CommunityMembersTable
import tech.kekulta.domain.models.communities.Community
import tech.kekulta.domain.models.communities.CommunityId
import tech.kekulta.domain.models.communities.CommunityInfo
import tech.kekulta.domain.models.events.EventId
import tech.kekulta.domain.models.users.Avatar
import tech.kekulta.domain.models.users.UserId

class CommunityService(private val database: Database) {

    suspend fun addMember(communityId: CommunityId, userId: UserId): Boolean = dbQuery {
        try {
            UserDao.findById(userId.id)?.let { user ->
                CommunityDao.findById(communityId.id)?.let { community ->
                    CommunityMembersTable.insert {
                        it[this.user] = user.id
                        it[this.community] = community.id
                    }.resultedValues?.size
                }
            } != null
        } catch (e: ExposedSQLException) {
            false
        }
    }

    suspend fun deleteMember(communityId: CommunityId, userId: UserId): Boolean = dbQuery {
        try {
            UserDao.findById(userId.id)?.let { user ->
                CommunityDao.findById(communityId.id)?.let { community ->
                    CommunityMembersTable.deleteWhere {
                        (CommunityMembersTable.user eq user.id) and (CommunityMembersTable.community eq community.id)
                    }
                }
            }?.let { it > 0 } ?: false
        } catch (e: ExposedSQLException) {
            false
        }
    }

    suspend fun getAllCommunities(): List<Community> = dbQuery {
        CommunityDao.all().map { it.toModel() }
    }

    suspend fun getCommunity(id: CommunityId): Community? = dbQuery {
        CommunityDao.findById(id.id)?.toModel()
    }

    suspend fun getCommunities(ids: List<CommunityId>): List<Community> = dbQuery {
        ids.mapNotNull { id ->
            CommunityDao.findById(id.id)?.toModel()
        }
    }

    suspend fun createCommunity(owner: UserId, info: CommunityInfo): Community? = dbQuery {
        try {
            UserDao.findById(owner.id)?.let { user ->
                CommunityDao.new {
                    this.owner = user.id
                    this.name = info.name
                    this.avatar = info.avatar.url
                    this.description = info.description
                }.toModel()
            }
        } catch (e: ExposedSQLException) {
            null
        }
    }

    suspend fun updateCommunity(id: CommunityId, info: CommunityInfo): Community? = dbQuery {
        try {
            CommunityDao.findById(id.id)?.apply {
                this.name = info.name
                this.avatar = info.avatar.url
                this.description = info.description
            }?.toModel()
        } catch (e: ExposedSQLException) {
            null
        }
    }

    suspend fun deleteCommunity(id: CommunityId): Boolean = dbQuery {
        try {
            CommunityDao.findById(id.id)?.delete() != null
        } catch (e: ExposedSQLException) {
            false
        }
    }

    private fun CommunityDao.toModel() = Community(
        id = CommunityId(id.value),
        owner = owner?.let { UserId(it.value) },
        members = members.map { dao -> UserId(dao.id.value) },
        events = events.map { dao -> EventId(dao.id.value) },
        info = CommunityInfo(
            name = name, description = description, avatar = Avatar(avatar),
        )
    )

    private suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(
        context = Dispatchers.IO, db = database,
    ) { block() }
}
