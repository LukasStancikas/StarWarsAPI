package com.anonymous.starwarsapp.network

import com.anonymous.starwarsapp.model.SWCharacterPage
import com.anonymous.starwarsapp.model.SWFilm
import com.anonymous.starwarsapp.model.SWStarship
import io.reactivex.Single

class ApiControllerImpl(
    private val api: StarWarsApi
) : ApiController {
    override fun getStarWarsFilm(id: String): Single<SWFilm> {
        return api.getStarWarsFilm(id)
    }

    override fun getStarWarsStarship(id: String): Single<SWStarship> {
        return api.getStarWarsStarship(id)
    }

    override fun getStarWarsCharacters(
        searchName: String?,
        page: Int
    ): Single<SWCharacterPage> {
        return api.getStarWarsCharacters(searchName, page)
    }
}