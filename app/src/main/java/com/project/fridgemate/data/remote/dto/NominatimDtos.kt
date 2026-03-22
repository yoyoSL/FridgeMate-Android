package com.project.fridgemate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NominatimResult(
    @SerializedName("display_name") val displayName: String,
    val lat: String,
    val lon: String,
    val address: NominatimAddress?
)

data class NominatimAddress(
    val city: String?,
    val town: String?,
    val village: String?,
    val country: String?,
    @SerializedName("country_code") val countryCode: String?
)
