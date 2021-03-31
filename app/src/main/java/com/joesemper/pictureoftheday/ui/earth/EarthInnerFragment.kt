package com.joesemper.pictureoftheday.ui.earth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.api.load
import com.joesemper.pictureoftheday.BuildConfig
import com.joesemper.pictureoftheday.R
import kotlinx.android.synthetic.main.fragment_earth_inner.*

class EarthInnerFragment(val data: EarthServerResponseData): Fragment() {

    companion object {
        fun newInstance(data: EarthServerResponseData) = EarthInnerFragment(data)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_earth_inner, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderData()
    }

    private fun renderData() {
        iv_earth.load(getPictureUrl()) {
            lifecycle(this@EarthInnerFragment)
            error(R.drawable.ic_load_error_vector)
            placeholder(R.drawable.ic_no_photo_vector)
        }

        tv_caption.text = data.caption

    }

    private fun getPictureUrl(): String {
        val basePictureUrl = "https://api.nasa.gov/EPIC/archive/enhanced/"
        val date = getDate()
        val extension = "png/"
        val img = data.image + ".png"
        val apiKey: String = "?api_key=" + BuildConfig.NASA_API_KEY

        return basePictureUrl + date + extension + img + apiKey
    }

    private fun getDate(): String{
        val pars = data.date.split("-", " ")
        return  "${pars[0]}/${pars[1]}/${pars[2]}/"
    }


}
