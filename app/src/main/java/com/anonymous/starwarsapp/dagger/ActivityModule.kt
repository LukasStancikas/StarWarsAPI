package com.anonymous.starwarsapp.dagger

import com.anonymous.starwarsapp.feature.characterlist.CharacterListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMainActivity(): CharacterListActivity
}