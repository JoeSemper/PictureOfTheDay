package com.joesemper.pictureoftheday.ui.earth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import coil.api.load
import com.joesemper.pictureoftheday.BuildConfig
import com.joesemper.pictureoftheday.R
import kotlinx.android.synthetic.main.fragment_earth_inner.*
import kotlinx.android.synthetic.main.fragment_main.*

class EarthInnerFragment(val data: EarthServerResponseData): Fragment() {

    companion object {
        fun newInstance(data: EarthServerResponseData) = EarthInnerFragment(data)
    }

    private var isExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_earth_inner, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        renderData()
//        setImageExpandClickListener()
    }

//    private fun renderData() {
//        iv_earth.load(getPictureUrl()) {
//            lifecycle(this@EarthInnerFragment)
//            error(R.drawable.ic_load_error_vector)
//            placeholder(R.drawable.ic_no_photo_vector)
//        }
//
//        tv_caption.text = data.caption
//
//    }

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

//    private fun setImageExpandClickListener() {
//        iv_earth.setOnClickListener {
//            isExpanded = !isExpanded
//
//            TransitionManager.beginDelayedTransition(container_earth, TransitionSet()
//                    .addTransition(ChangeBounds())
//                    .addTransition(ChangeImageTransform())
//            )
//
//            val params: ViewGroup.LayoutParams = iv_earth.layoutParams
//            params.height =
//                if (isExpanded) ViewGroup.LayoutParams.MATCH_PARENT
//                else ViewGroup.LayoutParams.WRAP_CONTENT
//
//            iv_earth.layoutParams = params
//            iv_earth.scaleType =
//                if (isExpanded) ImageView.ScaleType.CENTER_CROP
//                else ImageView.ScaleType.FIT_CENTER
//        }
//    }


}
