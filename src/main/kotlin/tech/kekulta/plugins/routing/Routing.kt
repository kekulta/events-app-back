package tech.kekulta.plugins.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import tech.kekulta.domain.models.communities.CommunityId
import tech.kekulta.domain.models.events.EventId
import tech.kekulta.domain.models.users.*
import tech.kekulta.plugins.EventsUserIdPrincipal

fun ApplicationCall.extractIdPrincipal() = principal<EventsUserIdPrincipal>()?.userId
fun ApplicationCall.extractUserId(name: String = "id") = parameters[name]?.toIntOrNull()?.let { UserId(it) }
fun ApplicationCall.extractEventId(name: String = "id") = parameters[name]?.toIntOrNull()?.let { EventId(it) }
fun ApplicationCall.extractCommunityId(name: String = "id") =
    parameters[name]?.toIntOrNull()?.let { CommunityId(it) }

fun ApplicationCall.queryUserId(name: String = "user_id") =
    request.queryParameters[name]?.toIntOrNull()?.let { UserId(it) }

fun ApplicationCall.queryEventId(name: String = "event_id") =
    request.queryParameters[name]?.toIntOrNull()?.let { EventId(it) }

fun ApplicationCall.queryCommunityId(name: String = "community_id") =
    request.queryParameters[name]?.toIntOrNull()?.let { CommunityId(it) }

fun ApplicationCall.queryNumber(name: String = "number") =
    request.queryParameters[name]?.let { number -> PhoneNumber(number) }

fun ApplicationCall.queryCode(name: String = "code") =
    request.queryParameters[name]?.let { number -> VerificationCodeValue(number) }

suspend fun ApplicationCall.responseOrNotFound(body: Any?) {
    if (body != null) {
        respond(HttpStatusCode.OK, body)
    } else {
        respond(HttpStatusCode.NotFound)
    }
}

suspend fun ApplicationCall.okOrNotFound(isOk: Boolean) {
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
    routing {
        get("/") {
            call.respondText("I'm awake!")
        }

        adminApis()
        registerApis()
        userApis()
        eventsApis()
        communityApis()
    }
}