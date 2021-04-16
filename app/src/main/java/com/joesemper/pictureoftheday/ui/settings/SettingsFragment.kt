package com.joesemper.pictureoftheday.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.joesemper.pictureoftheday.R
import com.joesemper.pictureoftheday.ui.picture.PictureOfTheDayFragment
import kotlinx.android.synthetic.main.fragment_settings.*

const val SETTINGS_FILE = "settings"
const val THEME_SETTINGS = "THEME"
const val THEME_GREEN = "GREEN"
const val THEME_BLUE = "BLUE"
const val THEME_RED = "RED"

class SettingsFragment : Fragment() {

    private var preferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initPreferences()
        requireActivity().setTheme(getCurrentTheme())
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initThemeChips()
    }

    private fun initThemeChips() {
        when (getCurrentTheme()) {
            R.style.AppTheme_Green -> (chipGroup_themes[0] as Chip).isChecked = true
            R.style.AppTheme_Blue -> (chipGroup_themes[1] as Chip).isChecked = true
            R.style.AppTheme_Red -> (chipGroup_themes[2] as Chip).isChecked = true
        }

        chipGroup_themes.setOnCheckedChangeListener { chipGroup, position ->
            chipGroup.findViewById<Chip>(position)?.let { chip ->
                when (chip.text) {
                    context?.getString(R.string.green) -> {
                        saveTheme(THEME_GREEN)
                        replaceFragment(SettingsFragment())
                    }
                    context?.getString(R.string.blue) -> {
                        saveTheme(THEME_BLUE)
                        replaceFragment(SettingsFragment())
                    }
                    context?.getString(R.string.red) -> {
                        saveTheme(THEME_RED)
                        replaceFragment(SettingsFragment())
                    }
                }
            }
        }
    }

    private fun initPreferences(){
        preferences = context?.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE)
    }

    private fun getCurrentTheme(): Int {
        return when (preferences?.getString(THEME_SETTINGS, THEME_GREEN)) {
            THEME_GREEN -> R.style.AppTheme_Green
            THEME_BLUE -> R.style.AppTheme_Blue
            THEME_RED -> R.style.AppTheme_Red
            else -> R.style.AppTheme_Green
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.container, fragment)
            ?.commit()
    }

    private fun saveTheme(theme: String) {
        preferences?.edit()?.putString(THEME_SETTINGS, theme)?.apply()
    }

}