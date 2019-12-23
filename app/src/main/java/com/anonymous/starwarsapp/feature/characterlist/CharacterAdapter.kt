package com.anonymous.starwarsapp.feature.characterlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anonymous.starwarsapp.R
import com.anonymous.starwarsapp.model.SWCharacter
import kotlinx.android.synthetic.main.item_character.view.*

class CharacterAdapter :
    PagedListAdapter<SWCharacter, CharacterAdapter.MyViewHolder>(CharacterDiffCallback()) {

    private var itemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        getItem(position)?.let { character ->
            holder.itemView.itemCharacterName.text = character.name
            holder.itemView.itemCharacterGender.text = character.gender
            holder.itemView.itemCharacterBirthYear.text = character.birthYear
            holder.itemView.setOnClickListener {
                itemClickListener?.onClick(character)
            }
        }
    }

    override fun getItemCount() = currentList?.size ?: 0

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface OnItemClickListener {
        fun onClick(item: SWCharacter)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private class CharacterDiffCallback : DiffUtil.ItemCallback<SWCharacter>() {

        override fun areItemsTheSame(oldItem: SWCharacter, newItem: SWCharacter): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: SWCharacter, newItem: SWCharacter): Boolean {
            return oldItem == newItem
        }
    }
}