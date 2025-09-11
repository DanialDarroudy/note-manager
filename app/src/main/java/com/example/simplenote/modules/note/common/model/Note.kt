package com.example.simplenote.modules.note.common.model

import com.google.gson.annotations.*
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Note(
    val id: Long,
    val title: String,
    val description: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("creator_name") val creatorName: String?,
    @SerializedName("creator_username") val creatorUserName: String?
)
