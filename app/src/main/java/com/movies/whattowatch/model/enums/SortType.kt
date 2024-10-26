package com.movies.whattowatch.model.enums

import com.movies.whattowatch.R

enum class SortType(val textID: Int) {
    POPULARITY(R.string.popular),
    VOTE_COUNT(R.string.votes),
    VOTE_AVERAGE(R.string.vote),
    REVENUE(R.string.revenue)
}