package com.notloco.android.data.models

import com.google.gson.annotations.SerializedName

// ==================== Razorpay ====================

// Razorpay Plans Response (matches actual API)
data class RazorpayPlansResponse(
    @SerializedName("plans")
    val plans: List<RazorpayPlan>?,
    @SerializedName("source")
    val source: String?
)

data class RazorpayPlan(
    @SerializedName("plan_id")
    val planId: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("period")
    val period: String?,
    @SerializedName("interval")
    val interval: Int?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("notes")
    val notes: List<String>?,
    @SerializedName("created_at")
    val createdAt: String?
)

// Razorpay Subscription Request
data class RazorpaySubscriptionRequest(
    @SerializedName("plan_id")
    val planId: String
)

// Razorpay Subscription Response
data class RazorpaySubscriptionResponse(
    @SerializedName("subscription_id")
    val subscriptionId: String?,
    @SerializedName("customer_id")
    val customerId: String?,
    @SerializedName("short_url")
    val shortUrl: String?,
    @SerializedName("status")
    val status: String?
)

// Razorpay Subscription Detail Response
data class RazorpaySubscriptionDetailResponse(
    @SerializedName("subscription_id")
    val subscriptionId: String?,
    @SerializedName("customer_id")
    val customerId: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("plan_details")
    val planDetails: RazorpayPlanDetails?,
    @SerializedName("start_at")
    val startAt: String?,
    @SerializedName("end_at")
    val endAt: String?,
    @SerializedName("ended_at")
    val endedAt: String?,
    @SerializedName("charge_at")
    val chargeAt: String?,
    @SerializedName("payment_method")
    val paymentMethod: String?,
    @SerializedName("total_count")
    val totalCount: Int?,
    @SerializedName("paid_count")
    val paidCount: Int?,
    @SerializedName("remaining_count")
    val remainingCount: Int?
)

data class RazorpayPlanDetails(
    @SerializedName("plan_id")
    val planId: String?,
    @SerializedName("plan_name")
    val planName: String?,
    @SerializedName("amount")
    val amount: Int?,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("period")
    val period: String?,
    @SerializedName("interval")
    val interval: Int?
)

// Razorpay Cancel Response
data class RazorpayCancelResponse(
    @SerializedName("subscription_id")
    val subscriptionId: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("message")
    val message: String?
)

// ==================== Stripe ====================

// Stripe Products Response
data class StripeProductsResponse(
    @SerializedName("data")
    val data: List<StripeProduct>?,
    @SerializedName("message")
    val message: String?
)

data class StripeProduct(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("price")
    val price: Double?,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("interval")
    val interval: String?,
    @SerializedName("interval_count")
    val intervalCount: Int?
)

// Stripe Subscription Response
data class StripeSubscriptionResponse(
    @SerializedName("subscription_id")
    val subscriptionId: String?,
    @SerializedName("client_secret")
    val clientSecret: String?,
    @SerializedName("message")
    val message: String?
)

// Stripe Setup Intent Response
data class StripeSetupIntentResponse(
    @SerializedName("client_secret")
    val clientSecret: String?,
    @SerializedName("message")
    val message: String?
)

// Stripe Subscription Detail Response
data class StripeSubscriptionDetailResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("plan_name")
    val planName: String?,
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("current_period_start")
    val currentPeriodStart: String?,
    @SerializedName("current_period_end")
    val currentPeriodEnd: String?,
    @SerializedName("cancel_at_period_end")
    val cancelAtPeriodEnd: Boolean?
)

enum class PaymentMethod(val value: String) {
    STRIPE("stripe"),
    RAZORPAY("razorpay")
}
