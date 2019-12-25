package com.anonymous.starwarsapp.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showShortSnackbar(message: String?) {
    view?.let {
        if (message != null) {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).apply {
                isGestureInsetBottomIgnored = true
                show()
            }
        }
    }
}

fun View.goneIf(gone: Boolean) {
    visibility = if (gone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}