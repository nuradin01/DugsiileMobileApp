package com.dugsiile.dugsiile.models


import com.google.gson.annotations.SerializedName
import java.util.*

data class FeeData(
    @SerializedName("amountCharged")
    val amountCharged: Float,
    @SerializedName("amountPaid")
    val amountPaid: Float?,
    @SerializedName("balance")
    val balance: Float?,
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
    val student: String,
    @SerializedName("user")
    val user: String
)