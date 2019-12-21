package com.anonymous.starwarsapp.model

import com.google.gson.annotations.SerializedName

data class SWCharacter(
    val name: String,
    val gender: String,
    @SerializedName("birth_year")
    val birthYear: String,
    val url: String
)