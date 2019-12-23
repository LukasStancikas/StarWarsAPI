package com.anonymous.starwarsapp.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anonymous.starwarsapp.R
import com.anonymous.starwarsapp.feature.characterdetails.CharacterDetailsFragment
import com.anonymous.starwarsapp.feature.characterlist.CharacterListFragment
import com.anonymous.starwarsapp.model.SWCharacter
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, CharacterListFragment())
        transaction.commit()
    }

    fun onSWCharacterClicked(character: SWCharacter) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, CharacterDetailsFragment.getInstance(character))
        transaction.addToBackStack(CharacterDetailsFragment.TAG)
        transaction.commit()
    }
}
