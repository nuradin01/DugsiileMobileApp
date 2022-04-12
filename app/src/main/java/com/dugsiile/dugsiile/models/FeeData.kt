package com.dugsiile.dugsiile.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*
@Parcelize
data class FeeData(
    @SerializedName("amountCharged")
    val amountCharged: Float,
    @SerializedName("amountPaid")
    val amountPaid: Float?,
    @SerializedName("balance")
    val balance: Float?,
    @SerializedName("chargedAt")
    val chargedAt: Date,
    @SerializedName("_id")
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
) : Parcelable