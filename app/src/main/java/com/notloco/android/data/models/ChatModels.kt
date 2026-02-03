package com.notloco.android.data.models

import com.google.gson.annotations.SerializedName

// Chat Message
data class ChatMessage(
    @SerializedName("id")
    val id: Int,
    @SerializedName("sender_id")
    val senderId: Int,
    @SerializedName("receiver_id")
    val receiverId: Int,
    @SerializedName("message")
    val message: String?,
    @SerializedName("audio_url")
    val audioUrl: String?,
    @SerializedName("video_url")
    val videoUrl: String?,
    @SerializedName("message_type")
    val messageType: String,
    @SerializedName("is_read")
    val isRead: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("sender_name")
    val senderName: String?,
    @SerializedName("sender_profile_pic")
    val senderProfilePic: String?
)

// User Chat Response
data class UserChatResponse(
    @SerializedName("data")
    val data: List<ChatMessage>,
    @SerializedName("message")
    val message: String?
)

// Send Message Request
data class SendMessageRequest(
    @SerializedName("receiver_id")
    val receiverId: Int,
    @SerializedName("message")
    val message: String?,
    @SerializedName("audio_url")
    val audioUrl: String?,
    @SerializedName("video_url")
    val videoUrl: String?,
    @SerializedName("message_type")
    val messageType: String
)

// Client Model (for coaches)
data class ClientModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("email")
    val email: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("profile_pic")
    val profilePic: String?,
    @SerializedName("is_verified")
    val isVerified: Boolean,
    @SerializedName("unread_count")
    val unreadCount: Int = 0,
    @SerializedName("is_special_member")
    val isSpecialMember: Boolean = false,
    @SerializedName("has_active_subscription")
    val hasActiveSubscription: Boolean = false
)

// Client List Response
data class ClientListResponse(
    @SerializedName("data")
    val data: List<ClientModel>,
    @SerializedName("message")
    val message: String?
)

// Coach Details
data class CoachDetails(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("profile_pic")
    val profilePic: String?,
    @SerializedName("bio")
    val bio: String?,
    @SerializedName("specialization")
    val specialization: String?
)

enum class MessageType(val value: String) {
    TEXT("text"),
    AUDIO("audio"),
    VIDEO("video"),
    IMAGE("image")
}
