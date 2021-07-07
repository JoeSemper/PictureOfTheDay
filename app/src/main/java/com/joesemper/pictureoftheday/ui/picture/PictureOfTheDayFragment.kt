package com.joesemper.pictureoftheday.ui.picture

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import coil.api.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.joesemper.pictureoftheday.R
import com.joesemper.pictureoftheday.ui.settings.*
import kotlinx.android.synthetic.main.main_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class PictureOfTheDayFragment : Fragment() {

    private var preferences: SharedPreferences? = null
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val viewModel: PictureOfTheDayViewModel by lazy {
        ViewModelProviders.of(this).get(PictureOfTheDayViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getData()
            .observe(this@PictureOfTheDayFragment, { renderContent(it) })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        preferences = context?.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE)
        requireActivity().setTheme(getCurrentTheme())
        return View.inflate(context, R.layout.main_fragment, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        setEndIconListener()
        initChips()
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
            R.id.item_settings -> navigateTo(SettingsFragment())
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateTo(fragment: Fragment): Boolean {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
        return true
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
                val url = serverResponseData?.url
                val text = serverResponseData?.explanation

                if (url.isNullOrEmpty()) {
                    //showError("Сообщение, что ссылка пустая")
                    toast("Link is empty")
                } else {
                    //showSuccess()
                    image_view.load(url) {
                        lifecycle(this@PictureOfTheDayFragment)
                        error(R.drawable.ic_load_error_vector)
                        placeholder(R.drawable.ic_no_photo_vector)
                    }
                    tv_explanation.text = text
                }
            }
            is PictureOfTheDayData.Loading -> {
                //showLoading()
            }
            is PictureOfTheDayData.Error -> {
                //showError(data.error.message)
                toast(data.error.message)
            }
        }
    }

    private fun setEndIconListener() {
        input_layout.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://en.wikipedia.org/wiki/${input_edit_text.text.toString()}")
            })
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

    companion object {
        fun newInstance() = PictureOfTheDayFragment()
        private var isMain = true
    }
}