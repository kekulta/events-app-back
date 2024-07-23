package tech.kekulta

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import tech.kekulta.data.db.sheme.*
import tech.kekulta.data.di.dataModule
import tech.kekulta.domain.models.PhoneNumber
import tech.kekulta.domain.models.RegistrationStatus
import tech.kekulta.domain.repositories.RegistrationDataRepository
import tech.kekulta.domain.repositories.RegistrationRepository
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

val AllTables = listOf(AccessTokensTable, ProfilesTable, RegisterTokensTable, UsersTable, VerificationCodesTable)

class RegistrationTest : KoinTest {
    @Before
    fun setup() {
        startKoin {
            allowOverride(true)
            modules(dataModule)
            module {
                Database.connect(
                    url = "jdbc:h2:mem:test", user = "root", driver = "org.h2.Driver", password = ""
                )
            }
        }
    }

    @After
    fun clenUp() {
        transaction {
            AllTables.forEach { it.deleteAll() }
        }
        stopKoin()
    }

    @Test
    fun testCreateUser() = runTest {
        val registrationRepository = get<RegistrationRepository>()
        val registrationDataRepository = get<RegistrationDataRepository>()

        launch {
            val number = PhoneNumber("1")
            val user = registrationRepository.startRegistration(number)
            assertNotNull(user)
            assertEquals(user.number, number)
            val statusById = registrationRepository.getRegistrationStatus(user.id)
            val statusByNumber = registrationRepository.getRegistrationStatus(number)
            assertEquals(statusById, statusByNumber)
            assertEquals(statusById, RegistrationStatus.CREATED)
        }
    }

    @Test
    fun testSendCode() = runTest {
        val registrationRepository = get<RegistrationRepository>()
        val registrationDataRepository = get<RegistrationDataRepository>()

        launch {
            val number = PhoneNumber("1")
            val user = registrationRepository.startRegistration(number)
            assertNotNull(user)

            val codesBefore = registrationDataRepository.getAllVerificationCodes()
            assert(codesBefore.isEmpty())

            val isSent = registrationRepository.sendCode(user.id)
            assert(isSent)

            val code = registrationDataRepository.getVerificationCode(user.id)
            assertNotNull(code)
            val codesAfter = registrationDataRepository.getAllVerificationCodes()
            assert(codesAfter.size == 1)
            assertContains(codesAfter, user.id to code)
        }
    }

    @Test
    fun testSendSameCode() = runTest {
        val registrationRepository = get<RegistrationRepository>()
        val registrationDataRepository = get<RegistrationDataRepository>()

        launch {
            val number = PhoneNumber("1")
            val user = registrationRepository.startRegistration(number)
            assertNotNull(user)

            val codesBefore = registrationDataRepository.getAllVerificationCodes()
            assert(codesBefore.isEmpty())

            repeat(5) {
                val isSent = registrationRepository.sendCode(user.id)
                assert(isSent)

                val code = registrationDataRepository.getVerificationCode(user.id)
                assertNotNull(code)
                val codesAfter = registrationDataRepository.getAllVerificationCodes()
                assert(codesAfter.size == 1)
                assertContains(codesAfter, user.id to code)
            }
        }
    }

    @Test
    fun testSendDifferentCodes() = runTest {
        val registrationRepository = get<RegistrationRepository>()
        val registrationDataRepository = get<RegistrationDataRepository>()

        launch {
            val codesBefore = registrationDataRepository.getAllVerificationCodes()
            assert(codesBefore.isEmpty())

            repeat(5) {
                val number = PhoneNumber(it.toString())
                val user = registrationRepository.startRegistration(number)
                assertNotNull(user)

                val isSent = registrationRepository.sendCode(user.id)
                assert(isSent)

                val code = registrationDataRepository.getVerificationCode(user.id)
                assertNotNull(code)
                val codesAfter = registrationDataRepository.getAllVerificationCodes()
                assert(codesAfter.size == it + 1)
                assertContains(codesAfter, user.id to code)
            }
        }
    }
}
