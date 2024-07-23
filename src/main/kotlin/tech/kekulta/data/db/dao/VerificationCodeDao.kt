package tech.kekulta.data.db.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import tech.kekulta.data.db.sheme.VerificationCodesTable

class VerificationCodeDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<VerificationCodeDao>(VerificationCodesTable)

    var code by VerificationCodesTable.code
}
