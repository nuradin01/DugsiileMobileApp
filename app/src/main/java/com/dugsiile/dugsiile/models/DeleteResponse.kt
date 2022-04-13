package com.dugsiile.dugsiile.models


import com.google.gson.annotations.SerializedName

data class DeleteResponse(
    @SerializedName("error")
    val error: String?,
    @SerializedName("success")
    val success: Boolean
)