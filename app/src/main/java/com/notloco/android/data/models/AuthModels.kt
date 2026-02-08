package com.notloco.android.data.models

import com.google.gson.annotations.SerializedName

// Login Request (also used for OTP verification)
data class LoginRequest(
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("otp")
    val otp: Int? = null
)

// Login Response (used for both login initiation and OTP verification)
data class LoginResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("ranking")
    val ranking: Int?,
    @SerializedName("is_verified")
    val isVerified: Boolean = false,
    @SerializedName("email")
    val email: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("token")
    val token: TokenData?,
    @SerializedName("otp_verification_status")
    val otpVerificationStatus: Boolean = false,
    @SerializedName("onboarding_complete")
    val onboardingComplete: Boolean = false,
    @SerializedName("message")
    val message: String?
)

data class TokenData(
    @SerializedName("access")
    val access: String
)

// Signup Request
data class SignupRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone_number")
    val phoneNumber: String
)

// Signup Response (returned directly, not wrapped)
data class SignupResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("ranking")
    val ranking: Int?,
    @SerializedName("is_verified")
    val isVerified: Boolean = false,
    @SerializedName("email")
    val email: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("is_member")
    val isMember: Boolean = false,
    @SerializedName("is_special_member")
    val isSpecialMember: Boolean = false,
    @SerializedName("free_trial_used")
    val freeTrialUsed: Boolean = false,
    @SerializedName("allow_journal_access")
    val allowJournalAccess: Boolean = false,
    @SerializedName("user_created")
    val userCreated: Boolean = false
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
