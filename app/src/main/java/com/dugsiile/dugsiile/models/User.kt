package com.dugsiile.dugsiile.models


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("data")
    val `data`: UserData,
    @SerializedName("success")
    val success: Boolean
)