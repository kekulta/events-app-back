package tech.kekulta.plugins.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import tech.kekulta.domain.models.communities.CommunityId
import tech.kekulta.domain.models.communities.CommunityInfo
import tech.kekulta.domain.repositories.*
import tech.kekulta.plugins.authenticateUser

fun Route.communityApis() {
    val communityRepository by inject<CommunityRepository>()

    authenticateUser {
        // Get List of communities
        get("/communities") {
            val ids = call.receive<List<CommunityId>>()
            val events = communityRepository.getCommunities(ids)

            call.respond(HttpStatusCode.OK, events)
        }

        // Read community
        get("/communities/{id}") {
            val id = call.extractCommunityId()
            val event = id?.let { communityRepository.getCommunity(id) }

            call.responseOrNotFound(event)
        }

        // Create new Community
        post("/communities/create") {
            val principal = call.extractIdPrincipal()
            val info = call.receive<CommunityInfo>()

            if (principal != null) {
                call.responseOrNotFound(communityRepository.createCommunity(principal, info))
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }


        // Update community
        put("/communities/{id}") {
            val communityId = call.extractCommunityId()
            val community = communityId?.let { communityRepository.getCommunity(it) }
            val principal = call.extractIdPrincipal()
            val info = call.receive<CommunityInfo>()

            if (principal != null && community?.owner == principal) {
                call.responseOrNotFound(communityRepository.updateCommunity(communityId, info))
            } else {
                call.respond(HttpStatusCode.Forbidden)
            }
        }


        // Delete community
        delete("/communities/{id}") {
            val communityId = call.extractCommunityId()
            val community = communityId?.let { communityRepository.getCommunity(it) }
            val principal = call.extractIdPrincipal()

            if (principal != null && community?.owner == principal) {
                call.okOrNotFound(communityRepository.deleteCommunity(communityId))
            } else {
                call.respond(HttpStatusCode.Forbidden)
            }
        }

        // Join community
        post("/communities/join/{id}") {
            val communityId = call.extractCommunityId()
            val community = communityId?.let { communityRepository.getCommunity(it) }
            val principal = call.extractIdPrincipal()

            if (principal != null && community != null) {
                val isSuccess = communityRepository.addMember(communityId, principal)

                call.okOrNotFound(isSuccess)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }


        // Leave community
        post("/communities/leave/{id}") {
            val communityId = call.extractCommunityId()
            val community = communityId?.let { communityRepository.getCommunity(it) }
            val principal = call.extractIdPrincipal()

            if (principal != null && community != null) {
                val isSuccess = communityRepository.deleteMember(communityId, principal)

                call.okOrNotFound(isSuccess)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }


        // Kick user from event
        post("/communities/kick/{id}") {
            val communityId = call.extractCommunityId()
            val community = communityId?.let { communityRepository.getCommunity(it) }
            val principal = call.extractIdPrincipal()
            val userToKick = call.queryUserId()

            if (userToKick != null && principal != null && community?.owner != null && community.owner == principal) {
                val isSuccess = communityRepository.deleteMember(communityId, userToKick)

                call.okOrNotFound(isSuccess)
            } else {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}
