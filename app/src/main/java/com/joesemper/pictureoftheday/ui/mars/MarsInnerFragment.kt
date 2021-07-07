package com.joesemper.pictureoftheday.ui.mars

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import coil.api.load
import com.joesemper.pictureoftheday.R
import kotlinx.android.synthetic.main.fragment_earth_inner.*
import kotlinx.android.synthetic.main.fragment_mars_inner.*
import java.text.SimpleDateFormat
import java.util.*

class MarsInnerFragment(private val position: Int): Fragment() {

    companion object {
        fun newInstance(position: Int) = MarsInnerFragment(position)
    }

    private val viewModel: MarsViewModel by lazy {
        ViewModelProviders.of(this).get(MarsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_mars_inner, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getData(getDate(-position-1))
            .observe(this@MarsInnerFragment, { renderData(it) })
    }

    private fun renderData(data: MarsData) {
        when (data) {
            is MarsData.Success -> {
                val serverResponseData = data.serverResponseData
                showContent(serverResponseData.data.first())
            }
            is MarsData.Error -> {
            }
            is MarsData.Loading -> {
            }
        }
    }

    private fun showContent(data: MarsPhotoData) {
        iv_mars.load(data.img) {
            lifecycle(this@MarsInnerFragment)
            error(R.drawable.ic_load_error_vector)
            placeholder(R.drawable.ic_no_photo_vector)
        }

        tv_mars_description.text = "Sol: ${data.sol}"
    }

    private fun getDate(amount: Int): String {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        calendar.add(Calendar.DAY_OF_MONTH, amount)
        return sdf.format(calendar.time).toString()
    }
}