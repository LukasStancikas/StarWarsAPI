package com.anonymous.starwarsapp.network

import com.anonymous.starwarsapp.model.SWCharacterPage
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface StarWarsApi {
    @GET("people/")
    fun getStarWarsCharacters(
        @Query("page") page: Int
    ): Single<SWCharacterPage>

    @GET("people/")
    fun searchForStarWarsCharacter(
        @Query("search") searchName: String,
        @Query("page") page: Int
    ): Single<SWCharacterPage>
}