package com.anonymous.starwarsapp.util

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showSnackbar(message: String?, duration: Int) {
    view?.let {
        if (message != null) {
            Snackbar.make(it, message, duration).show()
        }
    }
}