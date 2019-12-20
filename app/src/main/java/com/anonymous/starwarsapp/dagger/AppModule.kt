package com.anonymous.starwarsapp.dagger

import com.anonymous.starwarsapp.BuildConfig
import com.anonymous.starwarsapp.network.HeaderInterceptor
import com.anonymous.starwarsapp.network.StarWarsApi
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideStarWarsService(okHttpClient: OkHttpClient,
                               converterFactory: GsonConverterFactory
    ):StarWarsApi  =  Retrofit.Builder()
        .baseUrl("https://swapi.co/api/")
        .client(okHttpClient)
        .addConverterFactory(converterFactory)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create()


    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        headerInterceptor: HeaderInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addNetworkInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
        }


    @Provides
    fun provideHeaderInterceptor() = HeaderInterceptor()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)
}