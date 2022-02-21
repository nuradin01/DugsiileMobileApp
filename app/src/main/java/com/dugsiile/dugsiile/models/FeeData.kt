package com.dugsiile.dugsiile.models


import com.google.gson.annotations.SerializedName
import java.util.*

data class FeeData(
    @SerializedName("amountCharged")
    val amountCharged: Int,
    @SerializedName("amountPaid")
    val amountPaid: Int?,
    @SerializedName("balance")
    val balance: Int?,
    @SerializedName("chargedAt")
    val chargedAt: Date,
    @SerializedName("id")
    val id: String,
    @SerializedName("isPaid")
    val isPaid: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("paidAt")
    val paidAt: Date?,
    @SerializedName("student")
    val student: StudentData,
    @SerializedName("user")
    val user: String
)