package tech.kekulta.data.db.entity

import org.jetbrains.exposed.dao.EntityClass
import tech.kekulta.data.db.sheme.TokensTable

abstract class TokenEntityClass(table: TokensTable) :
    EntityClass<Int, TokenEntity>(table)
