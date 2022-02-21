package com.dugsiile.dugsiile.models


import com.google.gson.annotations.SerializedName

data class Fee(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val `data`: List<FeeData>,
    @SerializedName("success")
    val success: Boolean
)