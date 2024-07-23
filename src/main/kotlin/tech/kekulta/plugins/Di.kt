package tech.kekulta.plugins

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import tech.kekulta.data.di.dataModule


fun Application.configureDi() {
    install(Koin) {
        modules(dataModule)
    }
}
