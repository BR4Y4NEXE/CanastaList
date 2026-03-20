package com.example.canastalist.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entidad de Room para persistencia local.
 * Preparada con groupId y userId para futura sincronización en la nube.
 */
@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isChecked: Boolean = false,
    val quantity: String = "1",
    val reminderTimestamp: Long? = null,
    
    // Campos para escalabilidad a grupos familiares (Portafolio)
    val userId: String = "local_user",
    val groupId: String? = null,
    val authorName: String? = "Yo"
)
