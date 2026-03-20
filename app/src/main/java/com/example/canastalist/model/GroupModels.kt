package com.example.canastalist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un grupo familiar o compartido.
 */
@Entity(tableName = "shopping_groups")
data class ShoppingGroup(
    @PrimaryKey val id: String, // El código único generado (ej: AB12-CD34)
    val name: String,
    val isAdmin: Boolean = false
)

/**
 * Preferencias simples para saber qué grupo está activo.
 */
@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val id: Int = 0,
    val activeGroupId: String? = null,
    val userName: String? = "Usuario"
)
