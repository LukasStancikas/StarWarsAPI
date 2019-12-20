package com.anonymous.starwarsapp.dagger

import com.anonymous.starwarsapp.network.ApiController
import com.anonymous.starwarsapp.network.ApiControllerImpl
import com.anonymous.starwarsapp.network.StarWarsApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [AppModule::class])
class ControllerModule {

    @Singleton
    @Provides
    fun provideStarWarsService(starWarsApi: StarWarsApi): ApiController =
        ApiControllerImpl(starWarsApi)
}