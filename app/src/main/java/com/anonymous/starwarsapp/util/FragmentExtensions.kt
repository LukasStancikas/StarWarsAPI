package com.anonymous.starwarsapp.util

import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentArgumentDelegate<T : Any>(val key: String) :
    ReadOnlyProperty<Fragment, T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(
        thisRef: Fragment,
        property: KProperty<*>
    ): T {
        return thisRef.arguments
            ?.get(key) as? T
            ?: throw IllegalStateException("Property ${property.name} could not be read")
    }
}

fun <T : Any> Fragment.argument(key: String): ReadOnlyProperty<Fragment, T> =
    FragmentArgumentDelegate(key)