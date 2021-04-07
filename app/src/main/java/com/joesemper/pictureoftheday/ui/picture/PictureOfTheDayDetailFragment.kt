package com.joesemper.pictureoftheday.ui.picture

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.transition.*
import coil.api.load
import coil.request.CachePolicy
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.joesemper.pictureoftheday.R
import com.joesemper.pictureoftheday.ui.settings.*
import com.joesemper.pictureoftheday.util.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_pod_detail.*
import java.text.SimpleDateFormat
import java.util.*

class PictureOfTheDayDetailFragment : Fragment() {

    companion object {
        const val TRANSITION_NAME_BACKGROUND = "background"
        const val TRANSITION_NAME_CARD_CONTENT = "card_content"
        const val TRANSITION_NAME_DESCRIPTION_CONTENT = "description_content"
    }

    private var preferences: SharedPreferences? = null
    private var isExpanded = false

    private val args: PictureOfTheDayDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            createSharedElementTransition(LARGE_EXPAND_DURATION, R.id.detail_mirror)
        sharedElementReturnTransition =
            createSharedElementTransition(LARGE_COLLAPSE_DURATION, R.id.main_mirror)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        preferences = context?.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE)
        requireActivity().setTheme(getCurrentTheme())
        return View.inflate(context, R.layout.fragment_pod_detail, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val name: TextView = view.findViewById(R.id.tv_detail_header)
        val image: EquilateralImageView = view.findViewById(R.id.iv_detail)
        val background: NestedScrollView = view.findViewById(R.id.deatail_conteiner)
        val content: TextView = view.findViewById(R.id.tv_detail_description)
        val mirror: MirrorView = view.findViewById(R.id.detail_mirror)

        ViewCompat.setTransitionName(background, TRANSITION_NAME_BACKGROUND)
        ViewCompat.setTransitionName(mirror, TRANSITION_NAME_CARD_CONTENT)
        ViewCompat.setTransitionName(image, TRANSITION_NAME_DESCRIPTION_CONTENT)
        ViewGroupCompat.setTransitionGroup(background, true)

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->

            content.updatePadding(
                left = insets.systemWindowInsetLeft,
                right = insets.systemWindowInsetRight,
                bottom = insets.systemWindowInsetBottom
            )
            insets
        }

        renderData()
    }

    private fun renderData() {
        iv_detail.load(args.img) {
            lifecycle(this@PictureOfTheDayDetailFragment)
            networkCachePolicy(CachePolicy.ENABLED)
            error(R.drawable.ic_load_error_vector)
            placeholder(R.drawable.ic_no_photo_vector)
        }
        tv_detail_header.text = args.title
        tv_detail_description.text = args.description

        setImageExpandClickListener()
    }


    private fun setImageExpandClickListener() {
        iv_detail.setOnClickListener {
            isExpanded = !isExpanded

            TransitionManager.beginDelayedTransition(
                detail_content, TransitionSet()
                    .addTransition(ChangeBounds())
                    .addTransition(ChangeImageTransform())
            )

            val params: ViewGroup.LayoutParams = iv_detail.layoutParams
            params.height =
                if (isExpanded) ViewGroup.LayoutParams.MATCH_PARENT
                else ViewGroup.LayoutParams.WRAP_CONTENT

            iv_detail.layoutParams = params
            iv_detail.scaleType =
                if (isExpanded) ImageView.ScaleType.CENTER_CROP
                else ImageView.ScaleType.FIT_CENTER
        }
    }

    private fun getCurrentTheme(): Int {
        return when (preferences?.getString(THEME_SETTINGS, THEME_GREEN)) {
            THEME_GREEN -> R.style.AppTheme_Green
            THEME_BLUE -> R.style.AppTheme_Blue
            THEME_RED -> R.style.AppTheme_Red
            else -> R.style.AppTheme_Green
        }
    }

    private fun createSharedElementTransition(duration: Long, @IdRes noTransform: Int): Transition {
        return transitionTogether {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            this += SharedFade()
            this += ChangeBounds()
            this += ChangeTransform()
                // The content is already transformed along with the parent. Exclude it.
                .excludeTarget(noTransform, true)
        }
    }
}