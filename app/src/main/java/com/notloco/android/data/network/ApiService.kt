package com.notloco.android.data.network

import com.notloco.android.data.models.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==================== Authentication ====================
    // Login is used both for initiating login (without OTP) and verifying OTP (with OTP)
    @POST("user/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("user/signup/")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>

    @POST("user/token/refresh/")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>

    // ==================== User Profile ====================
    @GET("user/detail/")
    suspend fun getUserDetail(@Query("id") userId: Int): Response<UserInfo>

    @GET("user/profile/")
    suspend fun getUserProfile(@Query("id") userId: Int): Response<UserProfileResponse>

    @POST("user/profile/")
    suspend fun createUserProfile(
        @Query("id") userId: Int,
        @Body profile: UserProfileRequest
    ): Response<UserProfileResponse>

    @PUT("user/update/")
    suspend fun updateUser(
        @Query("id") userId: Int,
        @Body user: UserUpdateRequest
    ): Response<UserInfo>

    @DELETE("user/delete/")
    suspend fun deleteUser(): Response<Unit>

    @Multipart
    @POST("user/upload-profile-pic/")
    suspend fun uploadProfilePic(
        @Part profilePic: MultipartBody.Part
    ): Response<ProfilePicResponse>

    @GET("user/coach/")
    suspend fun getUserCoach(@Query("user_id") userId: Int): Response<CoachDetails>

    @POST("user/device/register")
    suspend fun registerDevice(@Body request: DeviceRegisterRequest): Response<Unit>

    // ==================== Journal ====================
    @GET("journal/journals/")
    suspend fun getJournalList(
        @Query("page") page: Int = 1,
        @Query("id") userId: Int? = null
    ): Response<JournalListResponse>

    @Multipart
    @POST("journal/upload-audio/")
    suspend fun uploadJournalAudio(
        @Part audio: MultipartBody.Part
    ): Response<JournalUploadResponse>

    @DELETE("journal/journals/")
    suspend fun deleteJournal(@Query("id") journalId: Int): Response<Unit>

    @PUT("journal/journals/")
    suspend fun updateJournalTranscription(
        @Query("id") journalId: Int,
        @Body request: UpdateTranscriptionRequest
    ): Response<JournalEntry>

    @PUT("journal/journals-hide/")
    suspend fun hideJournal(
        @Query("id") journalId: Int,
        @Query("status") status: Boolean
    ): Response<Unit>

    // ==================== Chat ====================
    @GET("chat/user/get-chats/")
    suspend fun getUserChats(@Query("is_pinned") isPinned: Boolean? = null): Response<ChatListResponse>

    @Multipart
    @POST("chat/user/send-message/")
    suspend fun userSendMessage(
        @Part audio: MultipartBody.Part? = null,
        @Query("reply_to_id") replyToId: Int? = null
    ): Response<ChatMessage>

    @PUT("chat/user/send-message/")
    suspend fun updateUserChat(
        @Query("id") messageId: Int,
        @Body request: UpdateChatRequest
    ): Response<ChatMessage>

    @DELETE("chat/delete-chat/")
    suspend fun deleteChat(@Query("message_id") messageId: Int): Response<Unit>

    @POST("chat/mark-as-read/")
    suspend fun markChatsAsRead(@Query("message_ids") messageIds: String): Response<Unit>

    // ==================== Coach ====================
    @GET("coach/health-coaches")
    suspend fun getHealthCoaches(): Response<List<CoachInfo>>

    @GET("coach/health-coaches/{coachId}")
    suspend fun getHealthCoachDetail(@Path("coachId") coachId: Int): Response<CoachInfo>

    @GET("coach/health-coaches/clients")
    suspend fun getCoachClients(): Response<ClientListResponse>

    @POST("coach/health-coaches/create/")
    suspend fun createHealthCoach(@Body request: CoachCreateRequest): Response<CoachInfo>

    @POST("coach/login/")
    suspend fun coachLogin(@Body request: LoginRequest): Response<JsonObject>

    @GET("chat/coach/get-chats/")
    suspend fun getCoachChats(
        @Query("user_id") userId: Int,
        @Query("is_pinned") isPinned: Boolean? = null
    ): Response<ChatListResponse>

    @Multipart
    @POST("chat/coach/send-message/")
    suspend fun coachSendMessage(
        @Query("user_id") userId: Int,
        @Part audio: MultipartBody.Part? = null,
        @Query("reply_to_id") replyToId: Int? = null
    ): Response<ChatMessage>

    @PUT("chat/coach/send-message/")
    suspend fun updateCoachChat(
        @Query("id") messageId: Int,
        @Body request: UpdateChatRequest
    ): Response<ChatMessage>

    @GET("journal/journals-coach/")
    suspend fun getCoachJournals(
        @Query("user_id") userId: Int,
        @Query("page") page: Int = 1
    ): Response<JournalListResponse>

    // ==================== Payments - Razorpay ====================
    @GET("payment/razorpay/plans/")
    suspend fun getRazorpayPlans(): Response<RazorpayPlansResponse>

    @POST("payment/razorpay/inititate-subscription/")
    suspend fun initiateRazorpaySubscription(@Body request: RazorpaySubscriptionRequest): Response<RazorpaySubscriptionResponse>

    @GET("payment/razorpay/subscription/detail/")
    suspend fun getRazorpaySubscriptionDetail(
        @Query("subscription_id") subscriptionId: String? = null
    ): Response<RazorpaySubscriptionDetailResponse>

    @GET("payment/razorpay/subscription/test/")
    suspend fun testRazorpaySubscription(): Response<JsonObject>

    @GET("payment/razorpay/webhook/")
    suspend fun getRazorpayWebhook(): Response<JsonObject>

    @DELETE("payment/razorpay/subscription/cancel/")
    suspend fun cancelRazorpaySubscription(
        @Query("subscription_id") subscriptionId: String
    ): Response<RazorpayCancelResponse>

    @POST("payment/razorpay/subscriptions/returning/")
    suspend fun resumeRazorpaySubscription(@Body request: RazorpayReturningSubscriptionRequest): Response<JsonObject>

    // ==================== Payments - Stripe ====================
    @GET("payment/products")
    suspend fun getStripeProducts(): Response<StripeProductsResponse>

    @GET("payment/inititate-subscription/")
    suspend fun initiateStripeSubscription(
        @Query("price_id") priceId: String
    ): Response<StripeSubscriptionResponse>

    @GET("payment/create-setup-intent/")
    suspend fun createStripeSetupIntent(
        @Query("price_id") priceId: String
    ): Response<StripeSetupIntentResponse>

    @GET("payment/subscription/detail/")
    suspend fun getStripeSubscriptionDetail(): Response<StripeSubscriptionDetailResponse>

    @POST("payment/create-checkout-session/")
    suspend fun createCheckoutSession(@Body request: CheckoutSessionRequest): Response<JsonObject>

    @POST("payment/subscription/cancel/")
    suspend fun cancelStripeSubscription(): Response<Unit>

    @POST("user/match-coach/")
    suspend fun matchCoach(@Body request: MatchCoachRequest): Response<JsonObject>

    // ==================== Common ====================
    @POST("common/get-presigned-url/")
    suspend fun getPresignedUrl(@Body request: PresignedUrlRequest): Response<PresignedUrlResponse>

    @GET("common/get-media")
    suspend fun getMedia(@Query("name") name: String): Response<MediaResponse>
}
