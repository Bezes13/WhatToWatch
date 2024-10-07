package com.example.whattowatch.dataClasses

import com.example.whattowatch.enums.UserMark

data class UserMovie(
    val movieId: Int = 0,
    val name: String = "",
    val isMovie: Boolean = true,
    val userMark: UserMark = UserMark.SEEN
)