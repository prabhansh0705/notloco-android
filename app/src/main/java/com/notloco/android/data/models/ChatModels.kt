package com.notloco.android.data.models

import com.google.gson.annotations.SerializedName

// Chat Message (matches actual API response)
data class ChatMessage(
    @SerializedName("id")
    val id: Int,
    @SerializedName("chat_session")
    val chatSession: Int?,
    @SerializedName("sender")
    val sender: Int?,
    @SerializedName("sender_type")
    val senderType: String?,
    @SerializedName("transcription")
    val transcription: String?,
    @SerializedName("audio_url")
    val audioUrl: String?,
    @SerializedName("audio_length")
    val audioLength: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("modified_at")
    val modifiedAt: String?,
    @SerializedName("is_edited")
    val isEdited: Boolean = false,
    @SerializedName("is_archived")
    val isArchived: Boolean = false,
    @SerializedName("is_vanish")
    val isVanish: Boolean = false,
    @SerializedName("is_read")
    val isRead: Boolean = false,
    @SerializedName("coach_profile_pic")
    val coachProfilePic: String?,
    @SerializedName("audio")
    val audio: String?,
    @SerializedName("expired_at")
    val expiredAt: String?,
    @SerializedName("reply_to")
    val replyTo: Int?,
    @SerializedName("replies")
    val replies: List<Int>?
)

// Chat List Response (matches actual API response)
data class ChatListResponse(
    @SerializedName("messages")
    val messages: List<ChatMessage>?,
    @SerializedName("page")
    val page: Int?,
    @SerializedName("total_pages")
    val totalPages: Int?
)

// Update Chat Request
data class UpdateChatRequest(
    @SerializedName("to_be_pinned")
    val toBePinned: Boolean? = null,
    @SerializedName("is_read")
    val isRead: Boolean? = null
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
    val isActive: Boolean = false,
    @SerializedName("profile_pic")
    val profilePic: String?,
    @SerializedName("is_verified")
    val isVerified: Boolean = false,
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
    val data: List<ClientModel>?,
    @SerializedName("message")
    val message: String?
)

// Coach Info (matches actual API response)
data class CoachInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("email")
    val email: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("is_active")
    val isActive: Boolean = false,
    @SerializedName("profile_pic")
    val profilePic: String?,
    @SerializedName("age")
    val age: Int?,
    @SerializedName("profession")
    val profession: String?,
    @SerializedName("years_experience")
    val yearsExperience: Int?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("pronoun")
    val pronoun: String?,
    @SerializedName("qualifications")
    val qualifications: String?,
    @SerializedName("expertise")
    val expertise: String?,
    @SerializedName("past_experience")
    val pastExperience: String?
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
    @SerializedName("profession")
    val profession: String?,
    @SerializedName("expertise")
    val expertise: String?
)

enum class MessageType(val value: String) {
    TEXT("text"),
    AUDIO("audio"),
    VIDEO("video"),
    IMAGE("image")
}
