package com.notloco.android.data.models

import com.google.gson.annotations.SerializedName

// Login Request
data class LoginRequest(
    @SerializedName("countryCode")
    val countryCode: String,
    @SerializedName("mobile")
    val mobile: String
)

// Login Response
data class LoginResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: LoginData?
)

data class LoginData(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("message")
    val message: String?
)

// Signup Request
data class SignupRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("countryCode")
    val countryCode: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("userType")
    val userType: String = "user"
)

// Signup Response
data class SignupResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: SignupData?
)

data class SignupData(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("message")
    val message: String?
)

// Verify OTP Request
data class VerifyOTPRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("otp")
    val otp: String
)

// Verify OTP Response
data class VerifyOTPResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: VerifyOTPData?
)

data class VerifyOTPData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("user")
    val user: UserInfo
)

// User Info
data class UserInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("country_code")
    val countryCode: String?,
    @SerializedName("user_type")
    val userType: String?,
    @SerializedName("profile_pic")
    val profilePic: String?,
    @SerializedName("is_verified")
    val isVerified: Boolean = false,
    @SerializedName("has_active_subscription")
    val hasActiveSubscription: Boolean = false
)

// Refresh Token Request
data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)

// Refresh Token Response
data class RefreshTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)

// Error Response
data class ErrorResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("errors")
    val errors: Map<String, List<String>>?
)
