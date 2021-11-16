package com.jp.smnotestest.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    var id: Int?,
    @SerializedName("created_at") var createdAt: String?,
    @SerializedName("updated_at") var updatedAt: String?,
    var title: String,
    var description: String,
    var completed: Int,
    var saved: Int,
    var deleted: Int,
    @SerializedName("user_id") var userId: String?,
    var attachment: String?
): Parcelable
