package com.joesemper.pictureoftheday.ui.mars

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.joesemper.pictureoftheday.R
import com.joesemper.pictureoftheday.ui.settings.*
import com.joesemper.pictureoftheday.util.DepthPageTransformer
import kotlinx.android.synthetic.main.fragment_mars.*
import java.text.SimpleDateFormat
import java.util.*

class MarsFragment : Fragment() {

    private var preferences: SharedPreferences? = null

    private lateinit var viewPager: ViewPager2

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewPager()
        initTabs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        preferences = context?.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE)
        requireActivity().setTheme(getCurrentTheme())
        return View.inflate(context, R.layout.fragment_mars, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }

    private fun initToolbar() {
        with(activity as AppCompatActivity) {
            setSupportActionBar(toolbar_mars)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
        }
        toolbar_mars.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initTabs() {
        TabLayoutMediator(tabLayout_mars, viewPager) { tab, position ->
            tab.text = getDate(-position-1 )
        }.attach()
    }

    private fun initViewPager() {
        val fragmentManager = requireActivity().supportFragmentManager
        viewPager = requireActivity().findViewById(R.id.view_pager_mars)
        viewPager.setPageTransformer(DepthPageTransformer())
        viewPager.adapter = ScreenSlidePagerAdapter(fragmentManager, lifecycle)

    }

    private fun getDate(amount: Int): String {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        calendar.add(Calendar.DAY_OF_MONTH, amount)
        return sdf.format(calendar.time).toString()
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
    ) :
        FragmentStateAdapter(fm, lifecycle) {

        override fun getItemCount(): Int = 7

        override fun createFragment(position: Int): Fragment =
            MarsInnerFragment.newInstance(position)
    }
}