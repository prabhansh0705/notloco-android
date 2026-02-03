package com.notloco.android.data.network

import com.notloco.android.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Authentication
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOTPRequest): Response<VerifyOTPResponse>

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>

    @POST("auth/resend-otp")
    suspend fun resendOtp(@Body userId: Map<String, Int>): Response<LoginResponse>

    // User Profile
    @GET("user/profile")
    suspend fun getUserProfile(): Response<UserInfo>

    @PUT("user/profile")
    suspend fun updateUserProfile(@Body user: UserInfo): Response<UserInfo>

    @Multipart
    @POST("user/upload-profile-pic")
    suspend fun uploadProfilePic(
        @Part image: MultipartBody.Part
    ): Response<AudioUploadResponse>

    // Journal
    @GET("journal/list")
    suspend fun getJournalList(): Response<JournalListResponse>

    @GET("journal/{id}")
    suspend fun getJournalEntry(@Path("id") id: Int): Response<JournalEntry>

    @POST("journal/create")
    suspend fun createJournal(@Body request: CreateJournalRequest): Response<JournalEntry>

    @DELETE("journal/{id}")
    suspend fun deleteJournal(@Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("journal/upload-audio")
    suspend fun uploadJournalAudio(
        @Part audio: MultipartBody.Part
    ): Response<AudioUploadResponse>

    @POST("journal/presigned-url")
    suspend fun getPresignedUrl(@Body request: PresignedUrlRequest): Response<PresignedUrlResponse>

    @PUT("journal/media")
    suspend fun uploadJournalMedia(@Body request: JournalMediaRequest): Response<Unit>

    // Chat
    @GET("chat/messages/{userId}")
    suspend fun getChatMessages(@Path("userId") userId: Int): Response<UserChatResponse>

    @POST("chat/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<ChatMessage>

    @PUT("chat/mark-read/{messageId}")
    suspend fun markMessageAsRead(@Path("messageId") messageId: Int): Response<Unit>

    @DELETE("chat/{messageId}")
    suspend fun deleteMessage(@Path("messageId") messageId: Int): Response<Unit>

    // Coach/Client (for coaches)
    @GET("coach/clients")
    suspend fun getClientList(): Response<ClientListResponse>

    @GET("coach/client/{clientId}/journal")
    suspend fun getClientJournal(@Path("clientId") clientId: Int): Response<JournalListResponse>

    @GET("user/coach-details")
    suspend fun getCoachDetails(): Response<CoachDetails>

    // Membership/Payments
    @GET("payments/products")
    suspend fun getPaymentProducts(): Response<PaymentsProductsResponse>

    @GET("payments/razorpay-plans")
    suspend fun getRazorpayPlans(): Response<RazorpayPlansResponse>

    @POST("subscription/initiate")
    suspend fun initiateSubscription(@Body request: InitiateSubscriptionRequest): Response<InitiateSubscriptionResponse>

    @POST("subscription/setup-intent")
    suspend fun createSetupIntent(): Response<SetupIntentResponse>

    @GET("subscription/details")
    suspend fun getSubscriptionDetails(): Response<SubscriptionDetailsResponse>

    @POST("subscription/cancel")
    suspend fun cancelSubscription(@Body request: CancelSubscriptionRequest): Response<Unit>

    // Notifications
    @POST("notifications/device-token")
    suspend fun sendDeviceToken(@Body token: Map<String, String>): Response<Unit>
}
