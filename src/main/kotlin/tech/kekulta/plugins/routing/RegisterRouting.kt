package tech.kekulta.plugins.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import tech.kekulta.domain.models.users.ProfileInfo
import tech.kekulta.domain.repositories.RegistrationDataRepository
import tech.kekulta.domain.repositories.RegistrationRepository
import tech.kekulta.domain.repositories.UserRepository
import tech.kekulta.plugins.authenticateRegister

fun Route.registerApis() {
    val userRepository by inject<UserRepository>()
    val registrationRepository by inject<RegistrationRepository>()
    val registrationDataRepository by inject<RegistrationDataRepository>()

    post("/register/start") {
        val number = call.queryNumber()

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
        val number = call.queryNumber()

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
        val number = call.queryNumber()
        val code = call.queryCode()

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

    authenticateRegister {
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
}
