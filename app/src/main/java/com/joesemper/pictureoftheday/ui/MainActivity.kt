package com.joesemper.pictureoftheday.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.joesemper.pictureoftheday.R
import com.joesemper.pictureoftheday.ui.earth.EarthFragment
import com.joesemper.pictureoftheday.ui.mars.MarsFragment
import com.joesemper.pictureoftheday.ui.picture.PictureOfTheDayFragment
import com.joesemper.pictureoftheday.ui.settings.*
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(getCurrentTheme())
        setContentView(R.layout.main_activity)
        displayStartFragment()
        initBottomNav()
    }

    private fun displayStartFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, PictureOfTheDayFragment.newInstance())
            .commitNow()
    }

    private fun initBottomNav() {
        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_view_home -> navigateTo(PictureOfTheDayFragment.newInstance())
                R.id.bottom_view_earth -> navigateTo(EarthFragment())
                R.id.bottom_view_mars -> navigateTo(MarsFragment())
                else -> navigateTo(PictureOfTheDayFragment.newInstance())
            }
        }
    }

    private fun navigateTo(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
        return true
    }

    private fun getCurrentTheme(): Int {
        val preferences = getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE)
        return when (preferences?.getString(THEME_SETTINGS, THEME_GREEN)) {
            THEME_GREEN -> R.style.AppTheme_Green
            THEME_BLUE -> R.style.AppTheme_Blue
            THEME_RED -> R.style.AppTheme_Red
            else -> R.style.AppTheme_Green
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.last() is PictureOfTheDayFragment) {
            finish()
        }
        bottomNav.menu.findItem(R.id.bottom_view_home).isChecked = true
        navigateTo(PictureOfTheDayFragment.newInstance())
    }
}
