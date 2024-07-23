package tech.kekulta.data.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import tech.kekulta.data.db.createDatabase
import tech.kekulta.data.repositories.*
import tech.kekulta.data.services.EventService
import tech.kekulta.data.services.UserService
import tech.kekulta.data.services.RegistrationService
import tech.kekulta.domain.repositories.*

val dataModule = module {
    single { createDatabase() }

    singleOf(::UserService)
    singleOf(::RegistrationService)
    singleOf(::EventService)
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::RegistrationRepositoryImpl) bind RegistrationRepository::class
    singleOf(::RegistrationDataRepositoryImpl) bind RegistrationDataRepository::class
    singleOf(::RegisterTokenRepositoryImpl) bind RegisterTokenRepository::class
    singleOf(::AccessTokenRepositoryImpl) bind AccessTokenRepository::class
    singleOf(::EventRepositoryImpl) bind EventRepository::class
}
