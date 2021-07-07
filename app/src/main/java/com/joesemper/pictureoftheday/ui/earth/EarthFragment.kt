package com.joesemper.pictureoftheday.ui.earth

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.joesemper.pictureoftheday.R
import com.joesemper.pictureoftheday.ui.settings.*
import com.joesemper.pictureoftheday.util.DepthPageTransformer
import kotlinx.android.synthetic.main.fragment_earth.*
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.*

class EarthFragment : Fragment() {

    private var preferences: SharedPreferences? = null

    private lateinit var viewPager: ViewPager2


    private val viewModel: EarthViewModel by lazy {
        ViewModelProviders.of(this).get(EarthViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getData()
            .observe(this@EarthFragment, { renderContent(it) })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        preferences = context?.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE)
        requireActivity().setTheme(getCurrentTheme())
        return View.inflate(context, R.layout.fragment_earth, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }

    private fun initToolbar() {
        with(activity as AppCompatActivity) {
            setSupportActionBar(toolbar_earth)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
        }
        toolbar_earth.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun renderContent(data: EarthData) {
        when (data) {
            is EarthData.Success -> {
                val serverResponseData = data.serverResponseData
                initViewPager(serverResponseData)
                initTabs(serverResponseData)
            }
            is EarthData.Error -> {
            }
            is EarthData.Loading -> {
            }
        }
    }

    private fun initTabs(data: List<EarthServerResponseData>) {
        TabLayoutMediator(tabLayout_earth, viewPager) { tab, position ->
            tab.text = data[position].date
        }.attach()
    }


    private fun initViewPager(data: List<EarthServerResponseData>) {
        val fragmentManager = requireActivity().supportFragmentManager
        viewPager = requireActivity().findViewById(R.id.view_pager_earth)
        viewPager.setPageTransformer(DepthPageTransformer())
        viewPager.adapter = ScreenSlidePagerAdapter(fragmentManager, lifecycle, data)
    }

    private fun getCurrentTheme(): Int {
        return when (preferences?.getString(THEME_SETTINGS, THEME_GREEN)) {
            THEME_GREEN -> R.style.AppTheme_Green
            THEME_BLUE -> R.style.AppTheme_Blue
            THEME_RED -> R.style.AppTheme_Red
            else -> R.style.AppTheme_Green
        }
    }

    private inner class ScreenSlidePagerAdapter(
        fm: FragmentManager,
        lifecycle: Lifecycle,
        val data: List<EarthServerResponseData>
    ) :
        FragmentStateAdapter(fm, lifecycle) {

        override fun getItemCount(): Int = data.size

        override fun createFragment(position: Int): Fragment =
            EarthInnerFragment.newInstance(data[position])
    }

}