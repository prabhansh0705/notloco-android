package com.notloco.android.data.models

import com.google.gson.annotations.SerializedName

// Journal Entry (matches actual API response)
data class JournalEntry(
    @SerializedName("id")
    val id: Int,
    @SerializedName("transcription")
    val transcription: String?,
    @SerializedName("chatgpt_response")
    val chatgptResponse: String?,
    @SerializedName("audio_file_name")
    val audioFileName: String?,
    @SerializedName("audio_url")
    val audioUrl: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("modified_at")
    val modifiedAt: String?,
    @SerializedName("audio_length")
    val audioLength: String?
)

// Journal List Response (matches actual API response)
data class JournalListResponse(
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("transcriptions")
    val transcriptions: List<JournalEntry>?,
    @SerializedName("page")
    val page: String?,
    @SerializedName("total_pages")
    val totalPages: Int?
)

// Journal Upload Response
data class JournalUploadResponse(
    @SerializedName("uploaded_audio_file")
    val uploadedAudioFile: String?,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("audio_length")
    val audioLength: String?
)

// Update Transcription Request
data class UpdateTranscriptionRequest(
    @SerializedName("transcription")
    val transcription: String
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
    val presignedUrl: String?,
    @SerializedName("unique_file_name")
    val uniqueFileName: String?,
    @SerializedName("file_url")
    val fileUrl: String?
)

// Media Response
data class MediaResponse(
    @SerializedName("data")
    val data: Any?
)
