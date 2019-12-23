package com.anonymous.starwarsapp.dagger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anonymous.starwarsapp.feature.characterdetails.CharacterDetailsViewModel
import com.anonymous.starwarsapp.feature.characterlist.CharacterListViewModel

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CharacterListViewModel::class)
    abstract fun bindCharacterListViewModel(viewModel: CharacterListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CharacterDetailsViewModel::class)
    abstract fun bindCharacterDetailsViewModel(viewModel: CharacterDetailsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}