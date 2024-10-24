package com.movies.whattowatch.apiRepository

import android.content.Context
import com.movies.whattowatch.R
import com.movies.whattowatch.dataClasses.MovieInfo
import com.movies.whattowatch.dataClasses.Provider
import com.movies.whattowatch.enums.SortType
import com.movies.whattowatch.dto.CastDTO
import com.movies.whattowatch.dto.CompanyDTO
import com.movies.whattowatch.dto.CreditsDTO
import com.movies.whattowatch.dto.GenresDTO
import com.movies.whattowatch.dto.MovieAvailability
import com.movies.whattowatch.dto.MovieCreditsDTO
import com.movies.whattowatch.dto.MovieDTO
import com.movies.whattowatch.dto.MovieInfoDTO
import com.movies.whattowatch.dataClasses.Genre
import com.movies.whattowatch.dataClasses.MediaType
import com.movies.whattowatch.dto.VideoDTO
import com.movies.whattowatch.dto.VideoInfoDTO
import com.google.gson.Gson
import com.movies.whattowatch.dto.PersonDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Properties

class ApiRepository(private val context: Context) {

    private val client = OkHttpClient()

    suspend fun getMovies(
        page: Int,
        genre: List<Genre>,
        companies: List<Provider>,
        getMovies: Boolean,
        sortType: SortType
    ): List<MovieInfo> {
        val sorting = when (sortType) {
            SortType.POPULARITY -> "popularity"
            SortType.VOTE_COUNT -> "vote_count"
            SortType.VOTE_AVERAGE -> "vote_average"
            SortType.REVENUE -> "revenue"
        }
        val result =
            apiCall("https://api.themoviedb.org/3/discover/${if (getMovies) "movie" else "tv"}?include_adult=true&include_video=false&language=en-US&page=$page&sort_by=$sorting.desc&watch_region=DE${
                if (genre.isNotEmpty()) "&with_genres=${
                    genre.joinToString(
                        "|"
                    ) { it.id.toString() }
                }" else ""
            }&with_watch_providers=${
                companies.filter { it.show }.joinToString("|") { it.providerId.toString() }
            }")

        val movieDto = Gson().fromJson(result, MovieDTO::class.java)
        return movieDto.results.map { dto ->
            MovieInfo(
                id = dto.id,
                originalLanguage = dto.original_language ?: "",
                overview = dto.overview ?: "",
                popularity = dto.popularity ?: 0.0,
                voteAverage = dto.vote_average ?: 0.0,
                voteCount = dto.vote_count ?: 0,
                posterPath = dto.poster_path ?: "",
                title = dto.title ?: dto.name,
                releaseDate = dto.release_date ?: dto.first_air_date ?: "",
                isMovie = !dto.release_date.isNullOrBlank(),
                mediaType = MediaType.MOVIE
            )
        }
    }

    suspend fun getMovieDetails(movieId: Int, isMovie: Boolean): MovieInfo {
        val result =
            apiCall("https://api.themoviedb.org/3/${if (isMovie) "movie" else "tv"}/$movieId?language=en-US")
        val dto = Gson().fromJson(result, MovieInfoDTO::class.java)
        return MovieInfo(
            id = dto.id,
            originalLanguage = dto.original_language ?: "",
            overview = dto.overview ?: "",
            popularity = dto.popularity ?: 0.0,
            voteAverage = dto.vote_average ?: 0.0,
            voteCount = dto.vote_count ?: 0,
            posterPath = dto.poster_path ?: "",
            title = dto.title ?: dto.name,
            releaseDate = dto.release_date ?: dto.first_air_date ?: "",
            isMovie = !dto.release_date.isNullOrBlank(),
            mediaType = MediaType.MOVIE,
            revenue = dto.revenue?: 0,
            runtime = dto.runtime?: 0,
            numberEpisodes = dto.number_of_episodes?: 0,
            numberSeasons = dto.number_of_seasons?: 0
        )
    }

    suspend fun getCast(movieId: Int, isMovie: Boolean): List<CastDTO> {
        val result =
            apiCall("https://api.themoviedb.org/3/${if (isMovie) "movie" else "tv"}/$movieId/credits?language=en-US")

        val credits = Gson().fromJson(result, CreditsDTO::class.java)
        return credits.cast.filterIndexed { index, _ -> index < 10 }
    }

    suspend fun getProviders(movieId: Int, isMovie: Boolean): MovieAvailability {
        val result =
            apiCall("https://api.themoviedb.org/3/${if (isMovie) "movie" else "tv"}/$movieId/watch/providers")
        return Gson().fromJson(result, MovieAvailability::class.java)
    }

    suspend fun getGenres(isMovie: Boolean): GenresDTO {
        val result =
            apiCall("https://api.themoviedb.org/3/genre/${if (isMovie) "movie" else "tv"}/list?language=de")
        return Gson().fromJson(result, GenresDTO::class.java)
    }

    suspend fun getCompanies(savedProviders: List<Int>): List<Provider> {
        val result =
            apiCall("https://api.themoviedb.org/3/watch/providers/movie?language=en-US&watch_region=DE")
        val companyDTO = Gson().fromJson(result, CompanyDTO::class.java)
        return companyDTO.results.map { provider ->
            Provider(
                providerName = provider.provider_name,
                providerId = provider.provider_id,
                logoPath = provider.logo_path,
                priority = provider.display_priorities["DE"] ?: 999,
                savedProviders.contains(provider.provider_id),
                false
            )
        }
    }

    suspend fun getPersonDetails(personId: Int): PersonDTO {
        val result = apiCall("https://api.themoviedb.org/3/person/$personId?language=en-US")
        return Gson().fromJson(result, PersonDTO::class.java)
    }

    suspend fun getMovieCredits(personId: Int): List<MovieInfo> {
        val movieCreditsJSON =
            apiCall("https://api.themoviedb.org/3/person/$personId/movie_credits?language=en-US")
        val tvCreditsJSON =
            apiCall("https://api.themoviedb.org/3/person/$personId/tv_credits?language=en-US")
        val movieCredits = Gson().fromJson(movieCreditsJSON, MovieCreditsDTO::class.java)
        val tvCredits = Gson().fromJson(tvCreditsJSON, MovieCreditsDTO::class.java)
        val allCredits =
            movieCredits.cast.filter { credit -> !credit.title.isNullOrBlank() && !credit.poster_path.isNullOrBlank() } + tvCredits.cast.filter { credit ->
                !credit.character.isNullOrBlank() && !credit.character.contains("Self") && !credit.poster_path.isNullOrBlank()
            }
        return allCredits.sorted().filterIndexed { index, _ -> index < 20 }.map { dto ->
            MovieInfo(
                id = dto.id,
                originalLanguage = dto.original_language ?: "",
                overview = dto.overview ?: "",
                popularity = dto.popularity ?: 0.0,
                voteAverage = dto.vote_average ?: 0.0,
                voteCount = dto.vote_count ?: 0,
                posterPath = dto.poster_path ?: "",
                title = dto.title ?: dto.name,
                releaseDate = dto.release_date ?: dto.first_air_date ?: "",
                isMovie = !dto.release_date.isNullOrBlank(),
                mediaType = MediaType.MOVIE
            )
        }
    }

    suspend fun getVideo(movieId: Int, isMovie: Boolean): List<VideoInfoDTO> {
        val videoJSON =
            apiCall("https://api.themoviedb.org/3/${if (isMovie) "movie" else "tv"}/${movieId}/videos?language=en-US")
        val video = Gson().fromJson(videoJSON, VideoDTO::class.java)
        return video.results
    }

    suspend fun getSearch(text: String, page: Int): List<MovieInfo> {
        val json =
            apiCall("https://api.themoviedb.org/3/search/multi?query=$text&include_adult=false&language=en-US&page=$page")
        val movieDto = Gson().fromJson(json, MovieDTO::class.java)
        return movieDto.results.map { dto ->
            MovieInfo(
                id = dto.id,
                originalLanguage = dto.original_language ?: "",
                overview = dto.overview ?: "",
                popularity = dto.popularity ?: 0.0,
                voteAverage = dto.vote_average ?: 0.0,
                voteCount = dto.vote_count ?: 0,
                posterPath = dto.poster_path ?: dto.profile_path ?: "",
                title = dto.title ?: dto.name,
                releaseDate = dto.release_date ?: dto.first_air_date ?: "",
                isMovie = !dto.release_date.isNullOrBlank(),
                mediaType = when (dto.media_type) {
                    "person" -> MediaType.PERSON
                    "tv" -> MediaType.TV
                    else -> MediaType.MOVIE
                },
                knownFor = dto.known_for?.map {
                    MovieInfo(
                        id = dto.id,
                        originalLanguage = dto.original_language ?: "",
                        overview = dto.overview ?: "",
                        popularity = dto.popularity ?: 0.0,
                        voteAverage = dto.vote_average ?: 0.0,
                        voteCount = dto.vote_count ?: 0,
                        posterPath = dto.poster_path ?: "",
                        title = dto.title ?: dto.name,
                        releaseDate = dto.release_date ?: dto.first_air_date ?: "",
                        isMovie = !dto.release_date.isNullOrBlank(),
                        mediaType = MediaType.MOVIE
                    )
                } ?: listOf()
            )
        }
    }

    private suspend fun readApiKeyFromConfigFile(): String {
        // Read API key from the configuration file in res/raw
        return withContext(Dispatchers.IO) {
            val inputStream = context.resources.openRawResource(R.raw.api_config)
            val properties = Properties()
            properties.load(inputStream)
            properties.getProperty("api_key") ?: throw IllegalArgumentException("API key not found")
        }
    }

    private suspend fun apiCall(url: String): String {
        return withContext(Dispatchers.IO) {
            val apiKey = readApiKeyFromConfigFile()

            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            val response = client.newCall(request).execute()
            response.body?.string() ?: ""
        }
    }
}



