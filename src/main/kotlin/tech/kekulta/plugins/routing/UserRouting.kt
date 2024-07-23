package tech.kekulta.plugins.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import tech.kekulta.domain.models.users.ProfileInfo
import tech.kekulta.domain.models.users.UserId
import tech.kekulta.domain.repositories.UserRepository
import tech.kekulta.plugins.authenticateUser

fun Route.userApis() {
    val userRepository by inject<UserRepository>()

    authenticateUser {
        get("/protected") {
            call.principal<UserIdPrincipal>()
            call.respondText(
                "Hello, ${call.extractIdPrincipal()?.id}!"
            )
        }

        // Get List of users
        get("/users") {
            val ids = call.receive<List<UserId>>()
            val users = userRepository.getProfiles(ids).map { profile -> profile.toDto() }

            call.respond(HttpStatusCode.OK, users)
        }

        // Read user
        get("/users/{id}") {
            val id = call.extractUserId()
            val profile = id?.let { userRepository.getProfile(id) }

            call.responseOrNotFound(profile?.toDto())
        }

        // Update user
        put("/users/{id}") {
            val id = call.extractUserId()
            val principal = call.extractIdPrincipal()
            val info = call.receive<ProfileInfo>()

            if (principal != null && id != null && id == principal) {
                val user = userRepository.updateProfile(id, info)

                call.responseOrNotFound(user?.toDto())
            } else {
                call.respond(HttpStatusCode.Forbidden, "You can't change another user info.")
            }

        }

        // Delete user
        delete("/users/{id}") {
            val id = call.extractUserId()
            val principal = call.extractIdPrincipal()

            if (principal != null && id != null && id == principal) {
                val isSuccess = userRepository.deleteUser(id)

                call.okOrNotFound(isSuccess)
            } else {
                call.respond(HttpStatusCode.Forbidden, "You can't delete another user.")
            }
        }
    }
}
