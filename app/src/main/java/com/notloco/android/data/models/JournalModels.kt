package com.notloco.android.data.models

import com.google.gson.annotations.SerializedName

// Journal Entry
data class JournalEntry(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("audio_url")
    val audioUrl: String?,
    @SerializedName("video_url")
    val videoUrl: String?,
    @SerializedName("transcript")
    val transcript: String?,
    @SerializedName("duration")
    val duration: Int?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

// Journal List Response
data class JournalListResponse(
    @SerializedName("data")
    val data: List<JournalEntry>,
    @SerializedName("message")
    val message: String?
)

// Create Journal Request
data class CreateJournalRequest(
    @SerializedName("title")
    val title: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("audio_url")
    val audioUrl: String?,
    @SerializedName("video_url")
    val videoUrl: String?,
    @SerializedName("duration")
    val duration: Int?
)

// Upload Audio Response
data class AudioUploadResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: AudioUploadData?
)

data class AudioUploadData(
    @SerializedName("audio_url")
    val audioUrl: String,
    @SerializedName("transcript")
    val transcript: String?
)

// Presigned URL Request
data class PresignedUrlRequest(
    @SerializedName("file_name")
    val fileName: String,
    @SerializedName("file_type")
    val fileType: String
)

// Presigned URL Response
data class PresignedUrlResponse(
    @SerializedName("presigned_url")
    val presignedUrl: String,
    @SerializedName("file_url")
    val fileUrl: String
)

// Delete Journal Request
data class DeleteJournalRequest(
    @SerializedName("journal_id")
    val journalId: Int
)

// Journal Media Request
data class JournalMediaRequest(
    @SerializedName("journal_id")
    val journalId: Int,
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("media_url")
    val mediaUrl: String
)
