package com.jp.smnotestest.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var id: Int?,
    @SerializedName("created_at") var createdAt: String?,
    @SerializedName("updated_at") var updatedAt: String?,
    var email: String,
    var password: String,
    var name: String,
    var deleted: Int
): Parcelable
