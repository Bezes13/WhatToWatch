package com.movies.whattowatch

import com.movies.whattowatch.model.dataClasses.MediaType
import com.movies.whattowatch.model.dataClasses.MovieInfo
import com.movies.whattowatch.model.dto.PersonDTO


object TestData  {
    val testPerson = PersonDTO(
        birthday = "1999-10-10",
        deathday = null,
        name = "Frederik",
        profile_path ="/9MRVpZpW8sGrEI4KYGkDrdFLTs4.jpg",
        id = 123465
    )
    val movie1 = MovieInfo(
        id = 533535,
        originalLanguage = "en",
        overview = "A listless Wade Wilson toils away in civilian life with his days as the morally flexible mercenary",
        popularity = 4025.483,
        posterPath = "/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg",
        releaseDate = "2024-07-24",
        title = "Deadpool & Wolverine",
        voteAverage = 7.74,
        voteCount = 4092,
        providerName = listOf(),
        isMovie = true,
        user = "",
        mediaType = MediaType.MOVIE,
        knownFor = listOf()
    )

    val movie2 = MovieInfo(
        id = 519182,
        originalLanguage = "en",
        overview = "Gru and Lucy and their girls — Margo",
        popularity = 1854.122,
        posterPath = "/wWba3TaojhK7NdycRhoQpsG0FaH.jpg",
        releaseDate = "2024 - 06 - 20",
        title = "Despicable Me 4",
        voteAverage = 7.141,
        voteCount = 1904,
        providerName = listOf(),
        isMovie = true,
        user = "",
        mediaType = MediaType.MOVIE,
        knownFor = listOf()
    )

    val movie3 = MovieInfo(
        id = 877817,
        originalLanguage = "en",
        overview = "Hired to cover up a high -profile crime",
        popularity = 1249.675,
        posterPath = "/vOX1Zng472PC2KnS0B9nRfM8aaZ.jpg",
        releaseDate = "2024-09-20",
        title = "Wolfs",
        voteAverage = 6.604,
        voteCount = 385,
        providerName = listOf("/2E03IAZsX4ZaUqM7tXlctEPMGWS.jpg"),
        isMovie = true,
        user = "",
        mediaType = MediaType.MOVIE,
        knownFor = listOf()
    )

    private val movie4 = MovieInfo(
        id = 957452,
        originalLanguage = "en",
        overview = "Soulmates Eric and Shelly are brutally murdered",
        popularity = 1329.937,
        posterPath = "/58QT4cPJ2u2TqWZkterDq9q4yxQ.jpg",
        releaseDate = "2024-08-21",
        title = "The Crow",
        voteAverage = 5.4,
        voteCount = 489,
        providerName = listOf(),
        isMovie = true,
        user = "",
        mediaType = MediaType.MOVIE,
        knownFor = listOf()
    )

    const val testGenre = "mappa"
    val testMovies = listOf(
        movie1,
        movie1,
        movie3,
        movie4
    )
    val genreWithMovies = Pair(
        testGenre, testMovies
    )
}

