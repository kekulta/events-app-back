package tech.kekulta.plugins.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import tech.kekulta.domain.repositories.CommunityRepository
import tech.kekulta.domain.repositories.EventRepository
import tech.kekulta.domain.repositories.RegistrationDataRepository
import tech.kekulta.domain.repositories.UserRepository
import tech.kekulta.plugins.authenticateAdmin

fun Route.adminApis() {
    val userRepository by inject<UserRepository>()
    val registrationDataRepository by inject<RegistrationDataRepository>()
    val eventRepository by inject<EventRepository>()
    val communityRepository by inject<CommunityRepository>()

    authenticateAdmin {
        get("/admin/register/codes") {
            call.respond(HttpStatusCode.OK, registrationDataRepository.getAllVerificationCodes())
        }

        get("/admin/register/tokens") {
            call.respond(HttpStatusCode.OK, registrationDataRepository.getAllRegisterTokens())
        }

        get("/admin/tokens") {
            call.respond(HttpStatusCode.OK, registrationDataRepository.getAllAccessTokens())
        }

        get("/admin/users") {
            call.respond(HttpStatusCode.OK, userRepository.getAllUsers())
        }

        get("/admin/profiles") {
            call.respond(HttpStatusCode.OK, userRepository.getAllProfiles())
        }

        get("/admin/events") {
            call.respond(HttpStatusCode.OK, eventRepository.getAllEvents())
        }

        get("/admin/communities") {
            call.respond(HttpStatusCode.OK, communityRepository.getAllCommunities())
        }
    }
}
