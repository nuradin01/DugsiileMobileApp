package com.dugsiile.dugsiile.models


import com.google.gson.annotations.SerializedName

data class ChargeSingleStudent(
    @SerializedName("data")
    val `data`: FeeData,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("error")
    val error: String?
)