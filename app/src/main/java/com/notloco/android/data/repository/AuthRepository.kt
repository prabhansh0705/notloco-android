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

    suspend fun verifyOtp(userId: Int, otp: String): Flow<Resource<VerifyOTPResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.verifyOtp(VerifyOTPRequest(userId, otp))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                data.data?.let { otpData ->
                    // Save auth tokens and user info
                    preferencesManager.saveAuthToken(otpData.accessToken)
                    preferencesManager.saveRefreshToken(otpData.refreshToken)
                    preferencesManager.saveUserId(otpData.user.id)
                    preferencesManager.saveUserName(otpData.user.name ?: "")
                    preferencesManager.saveUserEmail(otpData.user.email ?: "")
                    preferencesManager.saveUserPhone(otpData.user.phoneNumber ?: "")
                    preferencesManager.saveUserType(otpData.user.userType ?: "user")
                    preferencesManager.setLoggedIn(true)
                }
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.message() ?: "OTP verification failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun resendOtp(userId: Int): Flow<Resource<LoginResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.resendOtp(mapOf("user_id" to userId))
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
