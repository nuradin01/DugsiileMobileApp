package com.dugsiile.dugsiile.models


import com.google.gson.annotations.SerializedName

data class UploadImageResponse(
    @SerializedName("image")
    val image: String?,
    @SerializedName("success")
    val success: Boolean?
)