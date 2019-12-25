package com.anonymous.starwarsapp.feature.characterdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anonymous.starwarsapp.R
import com.anonymous.starwarsapp.model.SWFilm
import kotlinx.android.synthetic.main.item_film.view.*

class FilmAdapter : RecyclerView.Adapter<FilmAdapter.MyViewHolder>() {

    private val films = mutableListOf<SWFilm>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.itemFilmTitle.text = films[position].title
        holder.itemView.itemFilmDirector.text = films[position].director
        holder.itemView.itemFilmProducer.text = films[position].producer
        holder.itemView.itemFilmReleaseDate.text = films[position].releaseDate
        holder.itemView.itemFilmCharacterCount.text = films[position].characters.size.toString()
    }

    override fun getItemCount() = films.size

    fun setItems(newFilms: List<SWFilm>) {
        val diffResult = DiffUtil.calculateDiff(FilmDiffCallback(films, newFilms))
        films.clear()
        films.addAll(newFilms)
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private class FilmDiffCallback(val oldItems: List<SWFilm>, val newItems: List<SWFilm>) :
        DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].episodeId == newItems[newItemPosition].episodeId
        }

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }
    }
}