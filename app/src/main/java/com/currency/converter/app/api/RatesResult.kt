package com.currency.converter.app.api

import com.google.gson.annotations.SerializedName

data class RatesResult(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("date")
    val date: String,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("base")
    val base: String,
    @SerializedName("historical")
    val historical: Boolean = false,
    @SerializedName("rates")
    val rates: Map<String, Double>
)