package com.notloco.android.data.repository

import com.google.gson.JsonObject
import com.notloco.android.data.models.*
import com.notloco.android.data.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostmanRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun createHealthCoach(request: CoachCreateRequest): Flow<Resource<CoachInfo>> = flow {
        emitApiResult(apiService.createHealthCoach(request))
    }

    suspend fun coachLogin(phoneNumber: String, otp: Int?): Flow<Resource<JsonObject>> = flow {
        emitApiResult(apiService.coachLogin(LoginRequest(phoneNumber = phoneNumber, otp = otp)))
    }

    suspend fun updateCoachChat(messageId: Int, request: UpdateChatRequest): Flow<Resource<ChatMessage>> = flow {
        emitApiResult(apiService.updateCoachChat(messageId = messageId, request = request))
    }

    suspend fun matchCoach(request: MatchCoachRequest): Flow<Resource<JsonObject>> = flow {
        emitApiResult(apiService.matchCoach(request))
    }

    suspend fun createCheckoutSession(request: CheckoutSessionRequest): Flow<Resource<JsonObject>> = flow {
        emitApiResult(apiService.createCheckoutSession(request))
    }

    suspend fun resumeRazorpaySubscription(
        request: RazorpayReturningSubscriptionRequest
    ): Flow<Resource<JsonObject>> = flow {
        emitApiResult(apiService.resumeRazorpaySubscription(request))
    }

    suspend fun testRazorpaySubscription(): Flow<Resource<JsonObject>> = flow {
        emitApiResult(apiService.testRazorpaySubscription())
    }

    suspend fun getRazorpayWebhook(): Flow<Resource<JsonObject>> = flow {
        emitApiResult(apiService.getRazorpayWebhook())
    }

    private suspend fun <T> kotlinx.coroutines.flow.FlowCollector<Resource<T>>.emitApiResult(
        response: Response<T>
    ) {
        emit(Resource.Loading())
        if (response.isSuccessful && response.body() != null) {
            emit(Resource.Success(response.body()!!))
        } else {
            emit(Resource.Error(response.message().ifBlank { "API call failed" }))
        }
    }
}
