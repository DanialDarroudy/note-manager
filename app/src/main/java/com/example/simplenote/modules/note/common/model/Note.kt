package com.example.simplenote.modules.note.common.model

import android.os.Parcelable
import com.google.gson.annotations.*
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Note(
    val id: Long,
    val title: String,
    val description: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("creator_name") val creatorName: String?,
    @SerializedName("creator_username") val creatorUserName: String?
): Parcelable
