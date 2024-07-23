package tech.kekulta.data.db

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass

fun <ID, E : Entity<ID>, EC : EntityClass<ID, E>> EC.upsert(id: ID, update: E.() -> Unit): E {
    val entity = findById(id)

    println("id: $id, Entity: $entity")

    return if (entity == null) {
        new(id, update)
    } else {
        entity.update()
        entity
    }
}
