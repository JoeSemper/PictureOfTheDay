package com.joesemper.pictureoftheday.ui.earth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joesemper.pictureoftheday.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EarthViewModel(
    private val liveDataForViewToObserve: MutableLiveData<EarthData> = MutableLiveData(),
    private val retrofitImpl: EarthRetrofitImpl = EarthRetrofitImpl()
) :
    ViewModel() {

    fun getData(date: String = ""): LiveData<EarthData> {
        if (date.isBlank()){
            sendServerRequest()
        } else {
            sendServerRequestByDate(date)
        }
        return liveDataForViewToObserve
    }

    private fun sendServerRequest() {
        liveDataForViewToObserve.value = EarthData.Loading(null)
        val apiKey: String = BuildConfig.NASA_API_KEY

        if (apiKey.isBlank()) {
            EarthData.Error(Throwable("You need API key"))
        } else {
            retrofitImpl
                .getRetrofitImpl()
                .getEarthData(apiKey)
                .enqueue(object :
                    Callback<List<EarthServerResponseData>> {
                    override fun onResponse(
                        call: Call<List<EarthServerResponseData>>,
                        response: Response<List<EarthServerResponseData>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            liveDataForViewToObserve.value =
                                EarthData.Success(response.body()!!)
                        } else {
                            val message = response.message()
                            if (message.isNullOrEmpty()) {
                                liveDataForViewToObserve.value =
                                    EarthData.Error(Throwable("Unidentified error"))
                            } else {
                                liveDataForViewToObserve.value =
                                    EarthData.Error(Throwable(message))
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<EarthServerResponseData>>, t: Throwable) {
                        liveDataForViewToObserve.value = EarthData.Error(t)
                    }
                })
        }
    }

    private fun sendServerRequestByDate(date: String) {
        liveDataForViewToObserve.value = EarthData.Loading(null)
        val apiKey: String = BuildConfig.NASA_API_KEY

        if (apiKey.isBlank()) {
            EarthData.Error(Throwable("You need API key"))
        } else {
            retrofitImpl
                .getRetrofitImpl()
                .getEarthDataByDate(date, apiKey)
                .enqueue(object :
                    Callback<List<EarthServerResponseData>> {
                    override fun onResponse(
                        call: Call<List<EarthServerResponseData>>,
                        response: Response<List<EarthServerResponseData>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            liveDataForViewToObserve.value =
                                EarthData.Success(response.body()!!)
                        } else {
                            val message = response.message()
                            if (message.isNullOrEmpty()) {
                                liveDataForViewToObserve.value =
                                    EarthData.Error(Throwable("Unidentified error"))
                            } else {
                                liveDataForViewToObserve.value =
                                    EarthData.Error(Throwable(message))
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<EarthServerResponseData>>, t: Throwable) {
                        liveDataForViewToObserve.value = EarthData.Error(t)
                    }
                })
        }
    }
}