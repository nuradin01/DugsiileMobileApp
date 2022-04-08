package com.dugsiile.dugsiile.models


import com.google.gson.annotations.SerializedName
import java.util.*

data class StudentData(
    @SerializedName("fee")
    val fee: Float?,
    @SerializedName("fees")
    val fees: List<FeeData>?,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("id")
    val id: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("isLeft")
    val isLeft: Boolean?,
    @SerializedName("isScholarship")
    val isScholarship: Boolean?,
    @SerializedName("joinedAt")
    val joinedAt: Date?,
    @SerializedName("leftAt")
    val leftAt: Date?,
    @SerializedName("name")
    val name: String,
    @SerializedName("parentName")
    val parentName: String?,
    @SerializedName("parentPhone")
    val parentPhone: String?,
    @SerializedName("studentPhone")
    val studentPhone: String?,
    @SerializedName("studentSubjects")
    val studentSubjects: List<String>?,
    @SerializedName("user")
    val user: String?
)