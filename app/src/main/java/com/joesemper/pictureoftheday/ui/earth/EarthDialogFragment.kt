package com.joesemper.pictureoftheday.ui.earth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import coil.api.load
import coil.request.CachePolicy
import com.joesemper.pictureoftheday.R
import kotlinx.android.synthetic.main.fragment_dialog_earth.*
import kotlinx.android.synthetic.main.fragment_pod_detail.*

class EarthDialogFragment() : DialogFragment() {

    private var isExpanded = false

    companion object {
        private const val ARG = "DIALOG_ARG"

        fun newInstance(img: String) : EarthDialogFragment {
            val args = Bundle()
            args.putString(ARG, img)
            val fragment = EarthDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        return inflater.inflate(R.layout.fragment_dialog_earth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val img = arguments?.getString(ARG)

        iv_earth_dialog.load(img) {
            networkCachePolicy(CachePolicy.ENABLED)
            error(R.drawable.ic_load_error_vector)
            placeholder(R.drawable.ic_no_photo_vector)
        }
    }
}