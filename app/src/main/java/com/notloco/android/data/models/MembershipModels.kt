package com.notloco.android.data.models

import com.google.gson.annotations.SerializedName

// Payment Product
data class PaymentProduct(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("price")
    val price: Double,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("interval")
    val interval: String?,
    @SerializedName("interval_count")
    val intervalCount: Int?
)

// Payments Products Response
data class PaymentsProductsResponse(
    @SerializedName("data")
    val data: List<PaymentProduct>,
    @SerializedName("message")
    val message: String?
)

// Razorpay Plans Response
data class RazorpayPlansResponse(
    @SerializedName("data")
    val data: List<RazorpayPlan>,
    @SerializedName("message")
    val message: String?
)

data class RazorpayPlan(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("period")
    val period: String,
    @SerializedName("interval")
    val interval: Int
)

// Initiate Subscription Request
data class InitiateSubscriptionRequest(
    @SerializedName("plan_id")
    val planId: String,
    @SerializedName("payment_method")
    val paymentMethod: String
)

// Initiate Subscription Response
data class InitiateSubscriptionResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: SubscriptionData?
)

data class SubscriptionData(
    @SerializedName("subscription_id")
    val subscriptionId: String,
    @SerializedName("client_secret")
    val clientSecret: String?,
    @SerializedName("order_id")
    val orderId: String?
)

// Setup Intent Response (Stripe)
data class SetupIntentResponse(
    @SerializedName("client_secret")
    val clientSecret: String,
    @SerializedName("message")
    val message: String?
)

// Subscription Details Response
data class SubscriptionDetailsResponse(
    @SerializedName("data")
    val data: SubscriptionDetails?,
    @SerializedName("message")
    val message: String?
)

data class SubscriptionDetails(
    @SerializedName("id")
    val id: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("plan_name")
    val planName: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("current_period_start")
    val currentPeriodStart: String,
    @SerializedName("current_period_end")
    val currentPeriodEnd: String,
    @SerializedName("cancel_at_period_end")
    val cancelAtPeriodEnd: Boolean
)

// Cancel Subscription Request
data class CancelSubscriptionRequest(
    @SerializedName("subscription_id")
    val subscriptionId: String
)

enum class PaymentMethod(val value: String) {
    STRIPE("stripe"),
    RAZORPAY("razorpay")
}
