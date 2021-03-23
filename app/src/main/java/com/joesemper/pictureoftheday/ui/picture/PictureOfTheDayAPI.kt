package com.joesemper.pictureoftheday.ui.picture

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PictureOfTheDayAPI {

    @GET("planetary/apod")
    fun getPictureOfTheDay(@Query("api_key") apiKey: String, @Query("start_date") startDate: String = "2021-03-10"): Call<List<PODServerResponseData>>
}