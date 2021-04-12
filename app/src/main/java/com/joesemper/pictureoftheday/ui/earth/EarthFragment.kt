package com.joesemper.pictureoftheday.ui.earth

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.joesemper.pictureoftheday.BuildConfig
import com.joesemper.pictureoftheday.R
import com.joesemper.pictureoftheday.ui.earth.recycler.*
import com.joesemper.pictureoftheday.ui.settings.*
import kotlinx.android.synthetic.main.fragment_earth.*
import kotlinx.android.synthetic.main.main_activity.*
import java.util.*
import kotlin.concurrent.fixedRateTimer

class EarthFragment : Fragment() {

    private var preferences: SharedPreferences? = null


    private lateinit var adapter: EarthFragmentRVAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    private val viewModel: EarthViewModel by lazy {
        ViewModelProviders.of(this).get(EarthViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getData()
            .observe(this@EarthFragment as LifecycleOwner, { renderContent(it) })
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
                val rvData = createRVData(serverResponseData)
                initRV(rvData)
            }
            is EarthData.Error -> {
            }
            is EarthData.Loading -> {
            }
        }
    }

    private fun createRVData(data: List<EarthServerResponseData>): MutableList<Pair<Data, Boolean>> {
        return MutableList(data.size) { int ->
            val itemData = Data(
                img = getPictureUrl(data[int]),
                date = data[int].date,
                description = data[int].caption ?: "No description"
            )
            Pair(itemData, false)
        }
    }

    private fun initRV(data: MutableList<Pair<Data, Boolean>>) {
        val onListItemClickListener = object : OnListItemClickListener {
            override fun onItemClick(data: Data) {
//                val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
//                val prev = fragmentManager!!.findFragmentByTag("dialog")
//                if (prev != null) {
//                    ft.remove(prev)
//                }
//                ft.addToBackStack(null)

                val dialogFragment = EarthDialogFragment.newInstance(data.img)
                dialogFragment.show(childFragmentManager, "dialog")
            }
        }

        val onStartDragListener =  object : OnStartDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                itemTouchHelper.startDrag(viewHolder)
            }
        }

        adapter = EarthFragmentRVAdapter(onListItemClickListener, data, onStartDragListener)
        earthRecycler.adapter = adapter

        itemTouchHelper = ItemTouchHelper((ItemTouchHelperCallback(adapter)))
        itemTouchHelper.attachToRecyclerView(earthRecycler)

        earthRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(context, "Last", Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun getPictureUrl(data: EarthServerResponseData): String {
        val basePictureUrl = "https://api.nasa.gov/EPIC/archive/enhanced/"
        val date = getDate(data)
        val extension = "png/"
        val img = data.image + ".png"
        val apiKey: String = "?api_key=" + BuildConfig.NASA_API_KEY

        return basePictureUrl + date + extension + img + apiKey
    }

    private fun getDate(data: EarthServerResponseData): String {
        val pars = data.date.split("-", " ")
        return "${pars[0]}/${pars[1]}/${pars[2]}/"
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