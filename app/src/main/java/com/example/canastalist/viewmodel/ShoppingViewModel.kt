package com.example.canastalist.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.canastalist.data.ShoppingDao
import com.example.canastalist.model.ShoppingGroup
import com.example.canastalist.model.ShoppingItem
import com.example.canastalist.model.UserPreferences
import com.example.canastalist.receiver.ReminderReceiver
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

/**
 * ViewModel de Nivel Senior: Sincronización en Tiempo Real + Trazabilidad de Autoría.
 * Solucionado error de inicialización (NPE en preferences).
 */
class ShoppingViewModel(private val dao: ShoppingDao) : ViewModel() {
    
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val userId: String get() = auth.currentUser?.uid ?: "local_user"
    
    private var firebaseListener: ListenerRegistration? = null
    private var syncJob: Job? = null

    // IMPORTANTE: Definir las variables ANTES del bloque init
    val preferences: StateFlow<UserPreferences?> = dao.getUserPreferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val items: StateFlow<List<ShoppingItem>> = preferences
        .flatMapLatest { prefs -> dao.getItemsByGroup(prefs?.activeGroupId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groups: StateFlow<List<ShoppingGroup>> = dao.getAllGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }
        observeActiveGroupAndSync()
    }

    private fun observeActiveGroupAndSync() {
        syncJob = viewModelScope.launch {
            preferences.collect { prefs ->
                val groupId = prefs?.activeGroupId
                firebaseListener?.remove()
                
                if (groupId != null) {
                    firebaseListener = db.collection("groups").document(groupId)
                        .collection("items")
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) return@addSnapshotListener
                            snapshot?.let { querySnapshot ->
                                viewModelScope.launch {
                                    querySnapshot.documentChanges.forEach { change ->
                                        val doc = change.document
                                        val item = ShoppingItem(
                                            id = doc.id,
                                            name = doc.getString("name") ?: "",
                                            isChecked = doc.getBoolean("isChecked") ?: false,
                                            groupId = groupId,
                                            authorName = doc.getString("authorName") ?: "Anónimo"
                                        )
                                        when (change.type) {
                                            com.google.firebase.firestore.DocumentChange.Type.ADDED,
                                            com.google.firebase.firestore.DocumentChange.Type.MODIFIED -> dao.insertItem(item)
                                            com.google.firebase.firestore.DocumentChange.Type.REMOVED -> dao.deleteItem(item)
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }
    }

    fun setUserName(name: String) {
        viewModelScope.launch {
            val currentPrefs = preferences.value ?: UserPreferences()
            dao.savePreferences(currentPrefs.copy(userName = name))
        }
    }

    fun createGroup(name: String) {
        if (name.isNotBlank()) {
            viewModelScope.launch {
                val newCode = (1..4).map { (('A'..'Z') + ('0'..'9')).random() }.joinToString("") + "-" +
                               (1..4).map { (('A'..'Z') + ('0'..'9')).random() }.joinToString("")
                dao.insertGroup(ShoppingGroup(id = newCode, name = name, isAdmin = true))
                dao.savePreferences((preferences.value ?: UserPreferences()).copy(activeGroupId = newCode))
                db.collection("groups").document(newCode).set(mapOf("name" to name, "createdBy" to userId))
            }
        }
    }

    fun joinGroup(code: String) {
        val cleanCode = code.uppercase().trim()
        if (cleanCode.isNotBlank()) {
            viewModelScope.launch {
                try {
                    val doc = db.collection("groups").document(cleanCode).get().await()
                    if (doc.exists()) {
                        dao.insertGroup(ShoppingGroup(id = cleanCode, name = doc.getString("name") ?: "Grupo", isAdmin = false))
                        dao.savePreferences((preferences.value ?: UserPreferences()).copy(activeGroupId = cleanCode))
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    fun addItem(name: String) {
        if (name.isNotBlank()) {
            viewModelScope.launch {
                val currentGroupId = preferences.value?.activeGroupId
                val currentUserName = preferences.value?.userName ?: "Yo"
                val newItem = ShoppingItem(
                    name = name, 
                    groupId = currentGroupId, 
                    userId = userId,
                    authorName = currentUserName
                )
                dao.insertItem(newItem)
                if (currentGroupId != null) {
                    db.collection("groups").document(currentGroupId).collection("items").document(newItem.id).set(newItem)
                }
            }
        }
    }

    fun toggleItem(item: ShoppingItem) {
        viewModelScope.launch {
            val updated = item.copy(isChecked = !item.isChecked)
            dao.updateItem(updated)
            if (item.groupId != null) {
                db.collection("groups").document(item.groupId).collection("items").document(item.id).update("isChecked", updated.isChecked)
            }
        }
    }

    fun removeItem(item: ShoppingItem) {
        viewModelScope.launch {
            dao.deleteItem(item)
            if (item.groupId != null) {
                db.collection("groups").document(item.groupId).collection("items").document(item.id).delete()
            }
        }
    }

    fun switchGroup(groupId: String?) {
        viewModelScope.launch { 
            dao.savePreferences((preferences.value ?: UserPreferences()).copy(activeGroupId = groupId)) 
        }
    }

    fun setReminder(context: Context, item: ShoppingItem, timestamp: Long?) {
        viewModelScope.launch {
            dao.updateItem(item.copy(reminderTimestamp = timestamp))
            if (timestamp != null) {
                scheduleAlarm(context, item, timestamp)
            }
        }
    }

    private fun scheduleAlarm(context: Context, item: ShoppingItem, timestamp: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("ITEM_NAME", item.name)
            putExtra("ITEM_ID", item.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, item.id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
    }

    override fun onCleared() {
        super.onCleared()
        firebaseListener?.remove()
        syncJob?.cancel()
    }
}

class ShoppingViewModelFactory(private val dao: ShoppingDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
