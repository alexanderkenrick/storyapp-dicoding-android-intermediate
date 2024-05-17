package com.alexander.storyapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun saveAuthSession(user: UserEntity) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = user.token
            preferences[USER_ID] = user.userId
            preferences[NAME] = user.name
        }
    }

    suspend fun getAuthSession(): Flow<UserEntity> {
        return dataStore.data.map { preferences ->
            UserEntity(
                preferences[TOKEN].toString(),
                preferences[USER_ID].toString(),
                preferences[NAME].toString()
            )
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthPreferences? = null
        private val TOKEN = stringPreferencesKey("token")
        private val USER_ID = stringPreferencesKey("userId")
        private val NAME = stringPreferencesKey("name")

        fun getInstance(dataStore: DataStore<Preferences>): AuthPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}