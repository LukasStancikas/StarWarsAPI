package com.anonymous.starwarsapp.feature.characterdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anonymous.starwarsapp.R
import com.anonymous.starwarsapp.model.SWStarship
import kotlinx.android.synthetic.main.item_starship.view.*

class StarShipAdapter : RecyclerView.Adapter<StarShipAdapter.MyViewHolder>() {

    private val items = mutableListOf<SWStarship>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_starship, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.itemStarshipName.text = items[position].name
        holder.itemView.itemStarshipModel.text = items[position].model
        holder.itemView.itemStarshipClass.text = items[position].starshipClass
        holder.itemView.itemStarshipCost.text = items[position].costInCredits
        holder.itemView.itemStarshipHyperdrive.text = items[position].hyperdriveRating
        holder.itemView.itemStarshipManufacturer.text = items[position].manufacturer
        holder.itemView.itemStarshipPilotCount.text = items[position].pilots.size.toString()
    }

    override fun getItemCount() = items.size

    fun setItems(newItems: List<SWStarship>) {
        val diffResult = DiffUtil.calculateDiff(StarShipDiffCallback(items, newItems))
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private class StarShipDiffCallback(
        val oldItems: List<SWStarship>,
        val newItems: List<SWStarship>
    ) :
        DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].name == newItems[newItemPosition].name
        }

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }
    }
}