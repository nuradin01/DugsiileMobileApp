package com.dugsiile.dugsiile.models


import com.google.gson.annotations.SerializedName

data class Student(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val `data`: List<StudentData>,
    @SerializedName("success")
    val success: Boolean
)