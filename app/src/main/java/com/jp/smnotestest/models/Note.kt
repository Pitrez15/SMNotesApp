package com.jp.smnotestest.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = false) var id: Int?,
    @SerializedName("created_at") var createdAt: String?,
    @SerializedName("updated_at") var updatedAt: String?,
    var title: String,
    var description: String,
    var completed: Int,
    var saved: Int,
    var deleted: Int,
    @SerializedName("user_id") var userId: Int?,
    var attachment: String?
): Parcelable
