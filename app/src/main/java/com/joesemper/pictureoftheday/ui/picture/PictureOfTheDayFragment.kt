package com.joesemper.pictureoftheday.ui.picture

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.CaseMap
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.*
import coil.api.load
import coil.request.CachePolicy
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.joesemper.pictureoftheday.R
import com.joesemper.pictureoftheday.ui.settings.*
import com.joesemper.pictureoftheday.util.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.text.SimpleDateFormat
import java.util.*

class PictureOfTheDayFragment : Fragment() {

    private var preferences: SharedPreferences? = null
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val viewModel: PictureOfTheDayViewModel by lazy {
        ViewModelProviders.of(this).get(PictureOfTheDayViewModel::class.java)
    }

    private var currentData: PODServerResponseData? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getData()
            .observe(this@PictureOfTheDayFragment, { renderContent(it) })


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        preferences = context?.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE)
        requireActivity().setTheme(getCurrentTheme())
        return View.inflate(context, R.layout.fragment_main, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        setEndIconListener()
        initChips()
        initTransitionAnimation(view)
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar_home)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_app_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_settings -> {
                val host: NavHostFragment = requireActivity().supportFragmentManager
                    .findFragmentById(R.id.container) as NavHostFragment
                val navController = host.navController
                navController.navigate(R.id.settingsFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setEndIconListener() {
        input_layout.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://en.wikipedia.org/wiki/${input_edit_text.text.toString()}")
            })
        }
    }

    private fun initChips() {
        val calendar = Calendar.getInstance()
        for (i in 0 until chipGroup_days.childCount) {
            with(chipGroup_days[i] as Chip) {
                text = sdf.format(calendar.time).toString()
                if (i == 0) {
                    this.isChecked = true
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }
    }

    private fun renderContent(data: PictureOfTheDayData) {
        val calendar = Calendar.getInstance()
        renderData(data, sdf.format(calendar.time).toString())
        setChipsListener(data)
    }

    private fun setChipsListener(data: PictureOfTheDayData) {
        chipGroup_days.setOnCheckedChangeListener { chipGroup, position ->
            chipGroup.findViewById<Chip>(position)?.let {
                renderData(data, it.text.toString())
            }
        }
    }

    private fun renderData(data: PictureOfTheDayData, date: String) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                val serverResponseData = data.serverResponseData.find { it.date == date }
                currentData = serverResponseData
                setOnCardClickListener(serverResponseData!!)
                val url = serverResponseData?.url
                val text = serverResponseData?.title

                if (url.isNullOrEmpty()) {
                    toast("Link is empty")
                } else {
                    displayPOD(url, text!!)
                }
            }
            is PictureOfTheDayData.Loading -> {
            }
            is PictureOfTheDayData.Error -> {
                toast(data.error.message)
            }
        }
    }

    private fun displayPOD(url: String, title: String) {
        image_view_pod.load(url) {
            lifecycle(this@PictureOfTheDayFragment)
            networkCachePolicy(CachePolicy.ENABLED)
            error(R.drawable.ic_load_error_vector)
            placeholder(R.drawable.ic_no_photo_vector)
        }
        tv_main_header.text = title
    }

    private fun initTransition() {
        exitTransition = Fade(Fade.OUT).apply {
            duration = LARGE_EXPAND_DURATION / 2
            interpolator = FAST_OUT_LINEAR_IN
        }
        reenterTransition = Fade(Fade.IN).apply {
            duration = LARGE_COLLAPSE_DURATION / 2
            startDelay = LARGE_COLLAPSE_DURATION / 2
            interpolator = LINEAR_OUT_SLOW_IN
        }
    }

    private fun initTransitionAnimation(view: View) {
        val toolbar: MaterialToolbar = view.findViewById(R.id.toolbar_home)
        val content: NestedScrollView = view.findViewById(R.id.nestedScrollView)
        val card: CardView = view.findViewById(R.id.card_pod)
        val cardContent: ConstraintLayout = view.findViewById(R.id.pod_content)
        val image: EquilateralImageView = view.findViewById(R.id.image_view_pod)
        val title: TextView = view.findViewById(R.id.tv_main_header)
        val mirror: MirrorView = view.findViewById(R.id.main_mirror)

        ViewCompat.setOnApplyWindowInsetsListener(view.parent as View) { _, insets ->
            toolbar.updateLayoutParams<AppBarLayout.LayoutParams> {
                topMargin = insets.systemWindowInsetTop
            }
            content.updatePadding(
                left = insets.systemWindowInsetLeft,
                right = insets.systemWindowInsetRight,
                bottom = insets.systemWindowInsetBottom
            )
            insets
        }

        ViewCompat.setTransitionName(card, "card")
        ViewCompat.setTransitionName(cardContent, "card_content")
        ViewCompat.setTransitionName(mirror, "article")
        ViewGroupCompat.setTransitionGroup(cardContent, true)
    }

    private fun setOnCardClickListener(data: PODServerResponseData) {
        val card: CardView = requireActivity().findViewById(R.id.card_pod)
        val cardContent: ConstraintLayout = requireActivity().findViewById(R.id.pod_content)
        val mirror: MirrorView = requireActivity().findViewById(R.id.main_mirror)

        card.setOnClickListener { view ->
            val img = data.url!!
            val title = data.title!!
            val description = data.explanation!!

            view.findNavController().navigate(
                PictureOfTheDayFragmentDirections.actionPodDetail(img, title, description),

                FragmentNavigatorExtras(
                    card to PictureOfTheDayDetailFragment.TRANSITION_NAME_BACKGROUND,
                    cardContent to PictureOfTheDayDetailFragment.TRANSITION_NAME_CARD_CONTENT,
                    mirror to PictureOfTheDayDetailFragment.TRANSITION_NAME_DESCRIPTION_CONTENT
                )
            )
        }
    }

    private fun Fragment.toast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.BOTTOM, 0, 250)
            show()
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
}