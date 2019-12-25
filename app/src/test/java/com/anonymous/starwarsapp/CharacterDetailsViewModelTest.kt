package com.anonymous.starwarsapp

import com.anonymous.starwarsapp.feature.characterdetails.CharacterDetailsViewModel
import com.anonymous.starwarsapp.model.NetworkState
import com.anonymous.starwarsapp.model.SWCharacter
import com.anonymous.starwarsapp.model.SWFilm
import com.anonymous.starwarsapp.model.SWStarship
import com.anonymous.starwarsapp.network.ApiController
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CharacterDetailsViewModelTest {

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()

    @Mock
    private lateinit var apiController: ApiController

    @Mock
    private lateinit var character: SWCharacter

    @InjectMocks
    private lateinit var viewModel: CharacterDetailsViewModel

    @Test
    fun testFilms() {
        val testStream = viewModel.filmsDataStream.test()

        // Given
        whenever(apiController.getStarWarsFilm(TEST_ID_1)).thenReturn(Single.just(TEST_FILM))
        whenever(apiController.getStarWarsFilm(TEST_ID_2)).thenReturn(Single.just(TEST_FILM))
        whenever(character.getFilmIds()).thenReturn(listOf(TEST_ID_1, TEST_ID_2))

        // When
        viewModel.loadCharacterDetails(character)

        // Then
        testStream.assertValueAt(0, listOf(TEST_FILM, TEST_FILM))
    }

    @Test
    fun testStarships() {
        val testStream = viewModel.starshipsDataStream.test()

        // Given
        whenever(apiController.getStarWarsStarship(TEST_ID_1)).thenReturn(Single.just(TEST_STARSHIP))
        whenever(apiController.getStarWarsStarship(TEST_ID_2)).thenReturn(Single.just(TEST_STARSHIP))
        whenever(character.getStarshipIds()).thenReturn(listOf(TEST_ID_1, TEST_ID_2))

        // When
        viewModel.loadCharacterDetails(character)

        // Then
        testStream.assertValueAt(0, listOf(TEST_STARSHIP, TEST_STARSHIP))
    }

    @Test
    fun testLoading() {
        val testStream = viewModel.loadingStream.test()

        // Given
        whenever(apiController.getStarWarsStarship(TEST_ID_1)).thenReturn(Single.just(TEST_STARSHIP))
        whenever(apiController.getStarWarsFilm(TEST_ID_1)).thenReturn(Single.just(TEST_FILM))
        whenever(character.getStarshipIds()).thenReturn(listOf(TEST_ID_1))
        whenever(character.getFilmIds()).thenReturn(listOf(TEST_ID_1))

        // When
        viewModel.loadCharacterDetails(character)

        // Then
        testStream.assertValueAt(0, NetworkState.Loading)
        testStream.assertValueAt(1, NetworkState.Done)
    }

    @Test
    fun testLoadingFailed() {
        val testStream = viewModel.loadingStream.test()
        val error = Throwable("test error")
        // Given
        whenever(apiController.getStarWarsStarship(TEST_ID_1)).thenReturn(Single.just(TEST_STARSHIP))
        whenever(apiController.getStarWarsFilm(TEST_ID_1)).thenReturn(Single.error(error))
        whenever(character.getStarshipIds()).thenReturn(listOf(TEST_ID_1))
        whenever(character.getFilmIds()).thenReturn(listOf(TEST_ID_1))

        // When
        viewModel.loadCharacterDetails(character)

        // Then
        testStream.assertValueAt(0, NetworkState.Loading)
        testStream.assertValueAt(1, NetworkState.Error(error))
    }

    companion object {
        private const val TEST_ID_1 = "1"
        private const val TEST_ID_2 = "2"
        private val TEST_ITEM_LIST = listOf("a", "b")
        val TEST_FILM = SWFilm(
            "a",
            "b",
            "c",
            "d",
            "e",
            characters = TEST_ITEM_LIST
        )
        val TEST_STARSHIP = SWStarship(
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            pilots = TEST_ITEM_LIST
        )
    }
}
