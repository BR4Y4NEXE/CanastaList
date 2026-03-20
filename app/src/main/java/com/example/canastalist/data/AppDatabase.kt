package com.example.canastalist.data

import androidx.room.*
import com.example.canastalist.model.ShoppingGroup
import com.example.canastalist.model.ShoppingItem
import com.example.canastalist.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ShoppingDao {
    // Productos: Filtrados por el grupo activo (Portafolio profesional)
    @Query("SELECT * FROM shopping_items WHERE groupId = :groupId OR (groupId IS NULL AND :groupId IS NULL) ORDER BY isChecked ASC, id DESC")
    fun getItemsByGroup(groupId: String?): Flow<List<ShoppingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItem)

    @Update
    suspend fun updateItem(item: ShoppingItem)

    @Delete
    suspend fun deleteItem(item: ShoppingItem)

    // Grupos y Preferencias
    @Query("SELECT * FROM shopping_groups")
    fun getAllGroups(): Flow<List<ShoppingGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: ShoppingGroup)

    @Query("SELECT * FROM user_preferences WHERE id = 0")
    fun getUserPreferences(): Flow<UserPreferences?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(prefs: UserPreferences)
}

@Database(entities = [ShoppingItem::class, ShoppingGroup::class, UserPreferences::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingDao(): ShoppingDao
}
