package com.anonymous.starwarsapp.network

import com.anonymous.starwarsapp.model.SWCharacterPage
import io.reactivex.Single

class ApiControllerImpl(
    private val api: StarWarsApi
) : ApiController {
    override fun getStarWarsCharacters(
        searchName: String?,
        page: Int
    ): Single<SWCharacterPage> {
        return api.getStarWarsCharacters(searchName, page)
    }
}