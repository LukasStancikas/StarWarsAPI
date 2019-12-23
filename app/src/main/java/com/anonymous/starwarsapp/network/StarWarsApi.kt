package com.anonymous.starwarsapp.network

import com.anonymous.starwarsapp.model.SWCharacterPage
import com.anonymous.starwarsapp.model.SWFilm
import com.anonymous.starwarsapp.model.SWStarship
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StarWarsApi {
    @GET("people/")
    fun getStarWarsCharacters(
        @Query("search") searchName: String?,
        @Query("page") page: Int
    ): Single<SWCharacterPage>

    @GET("films/{id}")
    fun getStarWarsFilm(
        @Path("id") id: String
    ): Single<SWFilm>

    @GET("starships/{id}")
    fun getStarWarsStarship(
        @Path("id") id: String
    ): Single<SWStarship>
}