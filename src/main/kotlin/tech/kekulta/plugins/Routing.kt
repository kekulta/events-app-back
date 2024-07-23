package tech.kekulta.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import tech.kekulta.domain.models.*
import tech.kekulta.domain.repositories.RegistrationDataRepository
import tech.kekulta.domain.repositories.RegistrationRepository
import tech.kekulta.domain.repositories.UserRepository

private fun ApplicationCall.extractIdPrincipal() = principal<EventsUserIdPrincipal>()?.userId
private fun ApplicationCall.extractId(name: String = "id") = parameters[name]?.toIntOrNull()?.let { UserId(it) }

private suspend fun ApplicationCall.responseOrNotFound(body: Any?) {
    if (body != null) {
        respond(HttpStatusCode.OK, body)
    } else {
        respond(HttpStatusCode.NotFound)
    }
}

private suspend fun ApplicationCall.okOrNotFound(isOk: Boolean) {
    if (isOk) {
        respond(HttpStatusCode.OK)
    } else {
        respond(HttpStatusCode.NotFound)
    }
}

fun Profile.toDto(): UserDto {
    return UserDto(
        id = id,
        name = info.name,
        surname = info.surname,
        avatar = info.avatar,
    )
}

@Serializable
data class UserDto(val id: UserId, val name: String, val surname: String, val avatar: Avatar)

fun Application.configureRouting() {
    val userRepository by inject<UserRepository>()
    val registrationRepository by inject<RegistrationRepository>()
    val registrationDataRepository by inject<RegistrationDataRepository>()

    routing {
        get("/") {
            call.respondText("I'm awake!")
        }

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
        }

        post("/register/start") {
            val number = call.request.queryParameters["number"]?.let { number ->
                PhoneNumber(number)
            }

            if (number == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Number")
                return@post
            }

            val user = registrationRepository.startRegistration(number)

            if (user == null) {
                call.respond(HttpStatusCode.Forbidden, "Number already in use")
                return@post
            }

            val success = registrationRepository.sendCode(user.id)

            if (!success) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid UserId")
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    requireNotNull(registrationDataRepository.getVerificationCode(user.id))
                )
            }
        }

        post("/register/code") {
            val number = call.request.queryParameters["number"]?.let { number ->
                PhoneNumber(number)
            }

            if (number == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Number")
                return@post
            }

            val user = userRepository.getUser(number)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Unknown Number")
                return@post
            }

            val success = registrationRepository.sendCode(user.id)

            if (!success) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid UserId")
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    requireNotNull(registrationDataRepository.getVerificationCode(user.id))
                )
            }
        }

        post("/register/verify") {
            val number = call.request.queryParameters["number"]?.let { number -> PhoneNumber(number) }
            val code = call.request.queryParameters["code"]?.let { code -> VerificationCodeValue(code) }
            val user = number?.let { userRepository.getUser(number) }

            if (number != null && code != null && user != null) {
                val token = registrationRepository.checkCode(user.id, code)

                if (token != null) {
                    call.respond(HttpStatusCode.OK, mapOf("token" to token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        authenticate(Security.REGISTER) {
            post("/register/finish") {
                val id = call.extractIdPrincipal()
                val info = call.receive<ProfileInfo>()

                if (id != null) {
                    val token = registrationRepository.finishRegistration(id, info)
                    call.respond(HttpStatusCode.OK, mapOf("token" to token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }

        authenticate(Security.AUTH) {
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
                val id = call.extractId()
                val profile = id?.let { userRepository.getProfile(id) }

                call.responseOrNotFound(profile?.toDto())
            }

            // Update user
            put("/users/{id}") {
                val id = call.extractId()
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
                val id = call.extractId()
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
}