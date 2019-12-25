package com.anonymous.starwarsapp.model

data class SWCharacterPage(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<SWCharacter>
)