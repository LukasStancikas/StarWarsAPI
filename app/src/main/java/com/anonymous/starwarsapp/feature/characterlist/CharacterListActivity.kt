package com.anonymous.starwarsapp.feature.characterlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.anonymous.starwarsapp.R
import com.anonymous.starwarsapp.dagger.injectViewModel
import com.anonymous.starwarsapp.model.SWCharacterPage
import com.anonymous.starwarsapp.network.DataResult
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_character_list.*
import javax.inject.Inject

class CharacterListActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: CharacterListViewModel
    private val disposables = CompositeDisposable()

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_list)
        viewModel = injectViewModel(viewModelFactory)
    }

    override fun onStart() {
        super.onStart()
        subscribeToData()
        viewModel.refreshCharacters()
    }

    private fun subscribeToData() {
        viewModel
            .characterDataStream
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = ::showData
            )
            .addTo(disposables)
    }

    private fun showData(dataResult: DataResult<SWCharacterPage>) {
        when (dataResult.status) {
            DataResult.Status.SUCCESS -> {
                dataProgress.visibility = View.GONE
                dataText.text = dataResult.data?.count?.toString()
            }
            DataResult.Status.ERROR -> {
                dataProgress.visibility = View.GONE
                dataText.text = dataResult.message
            }
            DataResult.Status.LOADING -> {
                dataProgress.visibility = View.VISIBLE
                dataText.text = null
            }
        }
    }
}
