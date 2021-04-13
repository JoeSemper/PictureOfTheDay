package com.joesemper.pictureoftheday.ui.mars

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MarsAPI {

    @GET("rovers/Curiosity/photos")
    fun getMarsData(
        @Query("api_key") apiKey: String,
        @Query("earth_date") earthDate: String,
        @Query("camera") camera: String = "FHAZ"): Call<MarsServerResponseData>
}