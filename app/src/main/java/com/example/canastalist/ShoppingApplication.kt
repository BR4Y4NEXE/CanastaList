package com.example.canastalist

import android.app.Application
import androidx.room.Room
import com.example.canastalist.data.AppDatabase

/**
 * Clase Application para inicializar Room de forma global.
 */
class ShoppingApplication : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "canasta_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
}
