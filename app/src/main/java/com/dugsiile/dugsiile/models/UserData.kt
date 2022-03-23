package com.dugsiile.dugsiile.models


import com.google.gson.annotations.SerializedName
import java.util.*

data class UserData(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("password")
    val password: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("school")
    val school: String,
    @SerializedName("schoolSubjects")
    val schoolSubjects: List<String>,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isRegisteredSchool")
    val isRegisteredSchool: Boolean?,
    @SerializedName("joinedAt")
    val joinedAt: Date?
)