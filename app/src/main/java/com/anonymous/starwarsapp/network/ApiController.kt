package com.anonymous.starwarsapp.network

import com.anonymous.starwarsapp.model.SWCharacterPage
import io.reactivex.Single

interface ApiController {
    fun getStarWarsCharacters(searchName: String?, page: Int): Single<SWCharacterPage>
}