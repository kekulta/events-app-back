package tech.kekulta.plugins.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import tech.kekulta.domain.models.events.EventId
import tech.kekulta.domain.models.events.EventInfo
import tech.kekulta.domain.repositories.CommunityRepository
import tech.kekulta.domain.repositories.EventRepository
import tech.kekulta.plugins.authenticateUser

fun Route.eventsApis() {
    val eventRepository by inject<EventRepository>()
    val communityRepository by inject<CommunityRepository>()

    authenticateUser {
        // Get List of events
        get("/events") {
            val ids = call.receive<List<EventId>>()
            val events = eventRepository.getEvents(ids)

            call.respond(HttpStatusCode.OK, events)
        }

        // Read event
        get("/events/{id}") {
            val id = call.extractEventId()
            val event = id?.let { eventRepository.getEvent(id) }

            call.responseOrNotFound(event)
        }

        // Create event
        post("/events/create") {
            val principal = call.extractIdPrincipal()
            val communityId = call.queryCommunityId()
            val community = communityId?.let { communityRepository.getCommunity(communityId) }
            val info = call.receive<EventInfo>()

            if (principal != null && (community == null || (community.owner == principal))) {
                val event = eventRepository.createEvent(principal, communityId, info)

                if (event != null) {
                    call.respond(HttpStatusCode.OK, event)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            } else {
                call.respond(HttpStatusCode.Forbidden)
            }
        }

        // Update event
        put("/events/{id}") {
            val eventId = call.extractEventId()
            val communityId = call.queryCommunityId()
            val community = communityId?.let { communityRepository.getCommunity(communityId) }
            val principal = call.extractIdPrincipal()
            val info = call.receive<EventInfo>()
            val event = eventId?.let { eventRepository.getEvent(eventId) }

            if (principal != null && event?.owner != null && event.owner == principal) {
                when {
                    community == null -> {
                        call.responseOrNotFound(eventRepository.updateEvent(eventId, info))
                    }

                    community.owner == principal -> {
                        eventRepository.updateEvent(eventId, info)
                        call.responseOrNotFound(eventRepository.updateCommunity(eventId, communityId))
                    }

                    else -> {
                        call.respond(HttpStatusCode.Forbidden, "You can change only events you own.")
                    }
                }

            } else {
                call.respond(HttpStatusCode.Forbidden, "You can change only events you own.")
            }
        }

        // Delete event
        delete("/events/{id}") {
            val eventId = call.extractEventId()
            val principal = call.extractIdPrincipal()
            val event = eventId?.let { eventRepository.getEvent(eventId) }

            if (principal != null && event?.owner != null && event.owner == principal) {
                val isSuccess = eventRepository.deleteEvent(eventId)

                call.okOrNotFound(isSuccess)
            } else {
                call.respond(HttpStatusCode.Forbidden, "You can delete only events you own.")
            }
        }

        // Register to event
        post("/events/register/{id}") {
            val eventId = call.extractEventId()
            val principal = call.extractIdPrincipal()
            val event = eventId?.let { eventRepository.getEvent(eventId) }

            if (principal != null && event != null) {
                val isSuccess = eventRepository.addVisitor(eventId, principal)

                call.okOrNotFound(isSuccess)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }


        // Cancel registration
        post("/events/cancel/{id}") {
            val eventId = call.extractEventId()
            val principal = call.extractIdPrincipal()
            val event = eventId?.let { eventRepository.getEvent(eventId) }

            if (principal != null && event != null) {
                val isSuccess = eventRepository.deleteVisitor(eventId, principal)

                call.okOrNotFound(isSuccess)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }


        // Kick user from event
        post("/events/kick/{id}") {
            val eventId = call.extractEventId()
            val principal = call.extractIdPrincipal()
            val userToKick = call.queryUserId()
            val event = eventId?.let { eventRepository.getEvent(eventId) }

            if (userToKick != null && principal != null && event?.owner != null && event.owner == principal) {
                val isSuccess = eventRepository.deleteVisitor(eventId, userToKick)

                call.okOrNotFound(isSuccess)
            } else {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}
