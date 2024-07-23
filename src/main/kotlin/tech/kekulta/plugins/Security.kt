package tech.kekulta.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import tech.kekulta.domain.models.users.AccessToken
import tech.kekulta.domain.models.users.RegisterToken
import tech.kekulta.domain.models.users.UserId
import tech.kekulta.domain.repositories.AccessTokenRepository
import tech.kekulta.domain.repositories.RegisterTokenRepository

const val ADMIN_TOKEN = "admin-token"

data class EventsUserIdPrincipal(val userId: UserId) : Principal

object Security {
    const val AUTH = "auth-bearer"
    const val REGISTER = "register-bearer"
    const val ADMIN = "admin-bearer"
}

fun Application.configureSecurity() {
    val registerTokenRepository by inject<RegisterTokenRepository>()
    val accessTokensRepository by inject<AccessTokenRepository>()

    install(Authentication) {
        bearer(Security.REGISTER) {
            realm = "Access to the register account path"

            authenticate { tokenCredential ->
                val id = registerTokenRepository.checkToken(RegisterToken(tokenCredential.token))
                id?.let { EventsUserIdPrincipal(it) }
            }
        }

        bearer(Security.AUTH) {
            realm = "Access to the authenticated pages"

            authenticate { tokenCredential ->
                val id = accessTokensRepository.checkToken(AccessToken(tokenCredential.token))
                id?.let { EventsUserIdPrincipal(it) }
            }
        }

        bearer(Security.ADMIN) {
            realm = "Access to restricted APIs mainly for testing"

            authenticate { tokenCredential ->
                if (tokenCredential.token == ADMIN_TOKEN) {
                    EventsUserIdPrincipal(UserId(0))
                } else {
                    null
                }
            }
        }
    }
}

fun Route.authenticateRegister(
    build: Route.() -> Unit
): Route {
    return authenticate(
        configurations = arrayOf(Security.REGISTER),
        build = build
    )
}

fun Route.authenticateAdmin(
    build: Route.() -> Unit
): Route {
    return authenticate(
        configurations = arrayOf(Security.ADMIN),
        build = build
    )
}

fun Route.authenticateUser(
    build: Route.() -> Unit
): Route {
    return authenticate(
        configurations = arrayOf(Security.AUTH),
        build = build
    )
}

