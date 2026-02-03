package com.notloco.android.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID = intPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_PHONE = stringPreferencesKey("user_phone")
        private val USER_TYPE = stringPreferencesKey("user_type")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val FCM_TOKEN = stringPreferencesKey("fcm_token")
    }

    val authToken: Flow<String?> = dataStore.data.map { it[AUTH_TOKEN] }
    val refreshToken: Flow<String?> = dataStore.data.map { it[REFRESH_TOKEN] }
    val userId: Flow<Int?> = dataStore.data.map { it[USER_ID] }
    val userName: Flow<String?> = dataStore.data.map { it[USER_NAME] }
    val userEmail: Flow<String?> = dataStore.data.map { it[USER_EMAIL] }
    val userPhone: Flow<String?> = dataStore.data.map { it[USER_PHONE] }
    val userType: Flow<String?> = dataStore.data.map { it[USER_TYPE] }
    val isLoggedIn: Flow<Boolean> = dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val fcmToken: Flow<String?> = dataStore.data.map { it[FCM_TOKEN] }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { it[AUTH_TOKEN] = token }
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { it[REFRESH_TOKEN] = token }
    }

    suspend fun saveUserId(id: Int) {
        dataStore.edit { it[USER_ID] = id }
    }

    suspend fun saveUserName(name: String) {
        dataStore.edit { it[USER_NAME] = name }
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { it[USER_EMAIL] = email }
    }

    suspend fun saveUserPhone(phone: String) {
        dataStore.edit { it[USER_PHONE] = phone }
    }

    suspend fun saveUserType(type: String) {
        dataStore.edit { it[USER_TYPE] = type }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { it[IS_LOGGED_IN] = isLoggedIn }
    }

    suspend fun saveFcmToken(token: String) {
        dataStore.edit { it[FCM_TOKEN] = token }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
