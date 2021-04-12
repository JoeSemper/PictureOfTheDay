package com.joesemper.pictureoftheday.ui.earth.recycler

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.request.CachePolicy
import com.joesemper.pictureoftheday.R
import kotlinx.android.synthetic.main.fragmen_earth_header_item.view.*
import kotlinx.android.synthetic.main.fragment_earth_item.view.*

class EarthFragmentRVAdapter (
    private val onListItemClickListener: OnListItemClickListener,
    var data: MutableList<Pair<Data, Boolean>>,
    private val dragListener: OnStartDragListener
    ) :
        RecyclerView.Adapter<BaseViewHolder>(), ItemTouchHelperAdapter {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_EARTH = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater  = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(
                inflater.inflate(R.layout.fragmen_earth_header_item, parent, false) as View
            )
            else -> EarthViewHolder(
                inflater.inflate(R.layout.fragment_earth_item, parent, false) as View
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            data[position].first.img.isBlank() -> TYPE_HEADER
            data[position].first.img.isNotBlank() -> TYPE_EARTH
            else -> TYPE_EARTH
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        data.removeAt(fromPosition).apply {
            data.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }



    inner class EarthViewHolder(view: View) : BaseViewHolder(view),  ItemTouchHelperViewHolder {
        override fun bind(dataItem: Pair<Data, Boolean>) {
            itemView.iv_item_earth.load(dataItem.first.img) {
                networkCachePolicy(CachePolicy.ENABLED)
                error(R.drawable.ic_load_error_vector)
                placeholder(R.drawable.ic_no_photo_vector)
            }
            itemView.tv_item_description.text = dataItem.first.description
            itemView.tv_item_description.visibility = if (dataItem.second) View.VISIBLE else View.GONE
            itemView.tv_item_earth_time.text = dataItem.first.date
            itemView.iv_item_earth.setOnClickListener { onListItemClickListener.onItemClick(dataItem.first) }
            itemView.tv_item_earth_time.setOnClickListener{ toggleText() }
            itemView.iv_drag_handle.setOnTouchListener { _, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    dragListener.onStartDrag(this)
                }
                false
            }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(Color.WHITE)
        }

        private fun toggleText() {
            data[layoutPosition] = data[layoutPosition].let {
                it.first to !it.second
            }
            notifyItemChanged(layoutPosition)
        }


    }

    inner class HeaderViewHolder(view: View) : BaseViewHolder(view) {
        override fun bind(dataItem: Pair<Data, Boolean>) {
            itemView.tv_earth_header.text = dataItem.first.date
        }
    }
}

