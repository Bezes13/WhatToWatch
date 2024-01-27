package com.example.whattowatch.dataClasses

import com.example.whattowatch.enums.UserMark

data class UserMovie (val movieId: Int, val name: String, val isMovie: Boolean, val userMark: UserMark)