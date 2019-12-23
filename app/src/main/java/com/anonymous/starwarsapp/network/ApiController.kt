package com.anonymous.starwarsapp.network

import com.anonymous.starwarsapp.model.SWCharacterPage
import com.anonymous.starwarsapp.model.SWFilm
import com.anonymous.starwarsapp.model.SWStarship
import io.reactivex.Single

interface ApiController {
    fun getStarWarsCharacters(searchName: String?, page: Int): Single<SWCharacterPage>

    fun getStarWarsFilm(id: String): Single<SWFilm>

    fun getStarWarsStarship(id: String): Single<SWStarship>
}