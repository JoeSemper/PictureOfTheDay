package com.joesemper.pictureoftheday.ui.mars

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joesemper.pictureoftheday.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarsViewModel(
    private val liveDataForViewToObserve: MutableLiveData<MarsData> = MutableLiveData(),
    private val retrofitImpl: MarsRetrofitImpl = MarsRetrofitImpl()
) :
    ViewModel() {

    fun getData(date: String): LiveData<MarsData> {
        sendServerRequest(date)
        return liveDataForViewToObserve
    }

    private fun sendServerRequest(date: String) {
        liveDataForViewToObserve.value = MarsData.Loading(null)
        val apiKey: String = BuildConfig.NASA_API_KEY

        if (apiKey.isBlank()) {
            MarsData.Error(Throwable("You need API key"))
        } else {
            retrofitImpl
                .getRetrofitImpl()
                .getMarsData(earthDate = date, apiKey = apiKey)
                .enqueue(object :
                    Callback<MarsServerResponseData> {
                    override fun onResponse(
                        call: Call<MarsServerResponseData>,
                        response: Response<MarsServerResponseData>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            liveDataForViewToObserve.value =
                                MarsData.Success(response.body()!!)
                        } else {
                            val message = response.message()
                            if (message.isNullOrEmpty()) {
                                liveDataForViewToObserve.value =
                                    MarsData.Error(Throwable("Unidentified error"))
                            } else {
                                liveDataForViewToObserve.value =
                                    MarsData.Error(Throwable(message))
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<MarsServerResponseData>,
                        t: Throwable
                    ) {
                        liveDataForViewToObserve.value = MarsData.Error(t)
                    }
                })
        }
    }

}