package com.notloco.android.data.repository

import com.notloco.android.data.local.PreferencesManager
import com.notloco.android.data.models.*
import com.notloco.android.data.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {

    suspend fun login(phoneNumber: String): Flow<Resource<LoginResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.login(LoginRequest(phoneNumber))
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Login failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun signup(
        name: String,
        email: String,
        phoneNumber: String
    ): Flow<Resource<SignupResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.signup(
                SignupRequest(name, email, phoneNumber)
            )
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Signup failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    // OTP verification uses the login endpoint with OTP parameter
    suspend fun verifyOtp(phoneNumber: String, otp: String): Flow<Resource<LoginResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.login(LoginRequest(phoneNumber, otp.toIntOrNull()))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                // Save auth token and user info on successful OTP verification
                data.token?.let { token ->
                    preferencesManager.saveAuthToken(token.access)
                }
                preferencesManager.saveUserId(data.id)
                preferencesManager.saveUserName(data.name ?: "")
                preferencesManager.saveUserEmail(data.email ?: "")
                preferencesManager.saveUserPhone(data.phoneNumber ?: "")
                preferencesManager.setLoggedIn(true)
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.message() ?: "OTP verification failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    // Resend OTP by calling login without OTP (just phone number)
    suspend fun resendOtp(phoneNumber: String): Flow<Resource<LoginResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.login(LoginRequest(phoneNumber))
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "Failed to resend OTP"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun logout() {
        preferencesManager.clearAll()
    }

    fun isLoggedIn(): Flow<Boolean> = preferencesManager.isLoggedIn

    fun getUserType(): Flow<String?> = preferencesManager.userType
}
