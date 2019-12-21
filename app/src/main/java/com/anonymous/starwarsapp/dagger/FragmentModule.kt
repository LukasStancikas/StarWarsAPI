package com.anonymous.starwarsapp.dagger

import com.anonymous.starwarsapp.feature.characterlist.CharacterListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeThemeFragment(): CharacterListFragment
}