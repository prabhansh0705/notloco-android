package com.notloco.android.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_PHONE_KEY = stringPreferencesKey("user_phone")
        private val USER_TYPE_KEY = stringPreferencesKey("user_type")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }

    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] ?: false
    }

    val userType: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_TYPE_KEY]
    }

    suspend fun getAccessToken(): String? {
        return accessToken.first()
    }

    suspend fun saveAuthData(
        accessToken: String,
        refreshToken: String,
        userId: Int,
        email: String?,
        name: String?,
        phone: String?,
        userType: String
    ) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = email ?: ""
            preferences[USER_NAME_KEY] = name ?: ""
            preferences[USER_PHONE_KEY] = phone ?: ""
            preferences[USER_TYPE_KEY] = userType
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    suspend fun clearAuthData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun updateAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }

    suspend fun getRefreshToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }.first()
    }
}
