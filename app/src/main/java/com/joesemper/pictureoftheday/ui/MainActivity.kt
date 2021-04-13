package com.joesemper.pictureoftheday.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.joesemper.pictureoftheday.R
import com.joesemper.pictureoftheday.ui.earth.EarthFragment
import com.joesemper.pictureoftheday.ui.mars.MarsFragment
import com.joesemper.pictureoftheday.ui.picture.PictureOfTheDayFragment
import com.joesemper.pictureoftheday.ui.settings.*
import com.joesemper.pictureoftheday.util.EdgeToEdge
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(getCurrentTheme())
        setContentView(R.layout.main_activity)
        EdgeToEdge.setUpRoot(findViewById(R.id.container))
        initBottomNav()
    }

    private fun initBottomNav() {
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.container) as NavHostFragment? ?: return
        val navController = host.navController

        bottomNav.setupWithNavController(navController)
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
}
