package com.joesemper.pictureoftheday.ui.mars

import com.google.gson.annotations.SerializedName


data class MarsServerResponseData (
    @field:SerializedName("photos") val data: List<MarsPhotoData>,
)

data class MarsPhotoData (
    @field:SerializedName("img_src") val img: String,
    @field:SerializedName("earth_date") val earthDate: String,
    @field:SerializedName("sol") val sol: String,
        )