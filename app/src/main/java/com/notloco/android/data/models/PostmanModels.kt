package com.notloco.android.data.models

import com.google.gson.annotations.SerializedName

data class CoachCreateRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("age")
    val age: Int? = null,
    @SerializedName("profession")
    val profession: String? = null,
    @SerializedName("years_experience")
    val yearsExperience: Int? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("pronoun")
    val pronoun: String? = null,
    @SerializedName("qualifications")
    val qualifications: String? = null,
    @SerializedName("expertise")
    val expertise: String? = null,
    @SerializedName("past_experience")
    val pastExperience: String? = null,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean? = null,
    @SerializedName("is_staff")
    val isStaff: Boolean? = null,
    @SerializedName("onboarding_complete")
    val onboardingComplete: Boolean? = null,
    @SerializedName("is_verified")
    val isVerified: Boolean? = null
)

data class MatchCoachRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("coach_id")
    val coachId: Int
)

data class CheckoutSessionRequest(
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("uid")
    val uid: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("line1")
    val line1: String? = null,
    @SerializedName("postal_code")
    val postalCode: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("state")
    val state: String? = null,
    @SerializedName("country")
    val country: String? = null
)

data class RazorpayReturningSubscriptionRequest(
    @SerializedName("subscription_id")
    val subscriptionId: String? = null,
    @SerializedName("plan_id")
    val planId: String? = null
)
