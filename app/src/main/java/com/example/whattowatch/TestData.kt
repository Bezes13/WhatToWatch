package com.example.whattowatch

import com.example.whattowatch.dataClasses.MovieInfo


object TestData  {
    val movie1 = MovieInfo(
        231,
        "Englsich",
        "Toller Film",
        12,
        "pasdl",
        "22.02.2022",
        "Marsianer",
        123,
        123,
        listOf("Netflix"),

        false,
        "Abba"
    )

    val movie2 = MovieInfo(
        231,
        "Englsich",
        "Toller Film",
        12,
        "pasdl",
        "22.02.2022",
        "Marsianer",
        123,
        123,
        listOf("Netflix"),
        false
    )

    val movie3 = MovieInfo(
        231,
        "Englsich",
        "Puss in Boots discovers that his passion for adventure has taken its toll: He has burned through eight of his nine lives, leaving him with only one life left. Puss sets out on an epic journey to find the mythical Last Wish and restore his nine lives.",
        12,
        "pasdl",
        "2022",
        "Puss in Boots: The Last Wish",
        8.3,
        6891,
        listOf("Netflix"),
        true
    )

    val testGenre = "mappa"
    val genreWithMovies = Pair(
        testGenre, listOf(
            movie1,
            movie1,
            movie2,
        )
    )
}

