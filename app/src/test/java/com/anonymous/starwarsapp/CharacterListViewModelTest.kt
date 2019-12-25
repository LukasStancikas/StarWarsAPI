package com.anonymous.starwarsapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.anonymous.starwarsapp.feature.characterlist.CharacterListViewModel
import com.anonymous.starwarsapp.model.SWCharacterPage
import com.anonymous.starwarsapp.network.ApiController
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CharacterListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()

    @Mock
    private lateinit var apiController: ApiController

    @InjectMocks
    private lateinit var viewModel: CharacterListViewModel

    @Test
    fun testCharacterList() {
        // TODO test will not work, not sure how to handle PagingLibrary DataSource in Mockito Unit Tests

        val observer: Observer<Boolean> = mock()
        viewModel.emptyResultStream.observeForever(observer)

        // Given
        whenever(apiController.getStarWarsCharacters(any(), any())).thenReturn(
            Single.just(
                TEST_EMPTY_PAGE
            )
        )

        // When
        viewModel.searchForCharacter(SEARCH_QUERY)

        // Then
        val captor = ArgumentCaptor.forClass(Boolean::class.java)
        captor.run {
            verify(observer, times(1)).onChanged(capture())
            assertEquals(true, value)
        }
    }

    companion object {
        private const val SEARCH_QUERY = "test query"
        private val TEST_EMPTY_PAGE = SWCharacterPage(
            0,
            null,
            null,
            listOf()
        )
    }
}
