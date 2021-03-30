package com.joesemper.pictureoftheday.ui.earth


sealed class EarthData {
    data class Success(val serverResponseData: List<EarthServerResponseData>) : EarthData()
    data class Error(val error: Throwable) : EarthData()
    data class Loading(val progress: Int?) : EarthData()
}