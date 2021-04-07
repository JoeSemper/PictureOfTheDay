package com.joesemper.pictureoftheday.ui.earth

import com.google.gson.annotations.SerializedName

data class EarthServerResponseData(
    @field:SerializedName("caption") val caption: String?,
    @field:SerializedName("date") val date: String,
    @field:SerializedName("image") val image: String,
)
