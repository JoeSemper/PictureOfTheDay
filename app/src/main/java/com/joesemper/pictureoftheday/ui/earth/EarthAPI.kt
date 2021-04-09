package com.joesemper.pictureoftheday.ui.earth

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EarthAPI {

    @GET("api/enhanced")
    fun getEarthData(@Query("api_key") apiKey: String): Call<List<EarthServerResponseData>>
}