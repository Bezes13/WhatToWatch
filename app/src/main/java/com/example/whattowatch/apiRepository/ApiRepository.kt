package com.example.whattowatch.apiRepository

import android.content.Context
import com.example.whattowatch.dto.CastDTO
import com.example.whattowatch.dto.CompanyDTO
import com.example.whattowatch.dto.CompanyInfoDTO
import com.example.whattowatch.dto.CreditsDTO
import com.example.whattowatch.dto.SingleGenreDTO
import com.example.whattowatch.dto.GenresDTO
import com.example.whattowatch.dto.MovieAvailability
import com.example.whattowatch.dto.MovieDTO
import com.example.whattowatch.dto.MovieInfoDTO
import com.example.whattowatch.R
import com.example.whattowatch.dataClasses.MovieInfo
import com.example.whattowatch.dto.MovieCreditsDTO
import com.example.whattowatch.dto.VideoDTO
import com.example.whattowatch.dto.VideoInfoDTO
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Properties

class ApiRepository(private val context: Context) {

    private val client = OkHttpClient()

    suspend fun getMovies(
        page: Int,
        genre: SingleGenreDTO,
        companies: List<CompanyInfoDTO>
    ): List<MovieInfo> {
        val result =
            apiCall("https://api.themoviedb.org/3/discover/movie?include_adult=true&include_video=false&language=de-DE&page=$page&sort_by=popularity.desc&watch_region=DE&with_genres=${genre.id}&with_watch_providers=${
                companies.joinToString("|") { it.provider_id.toString() }
            }")
        val movieDto = Gson().fromJson(result, MovieDTO::class.java)
        return movieDto.results.map { dto ->
            MovieInfo(
                id = dto.id,
                originalLanguage = dto.original_language ?: "",
                overview = dto.overview ?: "",
                popularity = dto.popularity ?: 0,
                voteAverage = dto.vote_average ?: 0,
                voteCount = dto.vote_count ?: 0,
                posterPath = dto.poster_path ?: "",
                title = dto.title ?: dto.name,
                releaseDate = dto.release_date ?: dto.first_air_date ?: "",
                isMovie = !dto.release_date.isNullOrBlank()
            )
        }
    }

    suspend fun getMovieDetails(movieId: Int): MovieInfo {
        val result = apiCall("https://api.themoviedb.org/3/movie/$movieId?language=de-DE")
        val dto = Gson().fromJson(result, MovieInfoDTO::class.java)
        return MovieInfo(
            id = dto.id,
            originalLanguage = dto.original_language ?: "",
            overview = dto.overview ?: "",
            popularity = dto.popularity ?: 0,
            voteAverage = dto.vote_average ?: 0,
            voteCount = dto.vote_count ?: 0,
            posterPath = dto.poster_path ?: "",
            title = dto.title ?: dto.name,
            releaseDate = dto.release_date ?: dto.first_air_date ?: "",
            isMovie = !dto.release_date.isNullOrBlank()
        )
    }

    suspend fun getCast(movieId: Int, isMovie: Boolean): List<CastDTO> {

        val result =
            if (isMovie) apiCall("https://api.themoviedb.org/3/movie/$movieId/credits?language=de-DE")
            else
                apiCall("https://api.themoviedb.org/3/tv/$movieId/credits?language=de-DE")


        val credits = Gson().fromJson(result, CreditsDTO::class.java)
        return credits.cast.filterIndexed { index, _ -> index < 10 }
    }

    suspend fun getProviders(movieId: Int): MovieAvailability {
        val result = apiCall("https://api.themoviedb.org/3/movie/$movieId/watch/providers")
        return Gson().fromJson(result, MovieAvailability::class.java)

    }

    suspend fun getGenres(): GenresDTO {
        val result = apiCall("https://api.themoviedb.org/3/genre/movie/list?language=de")
        return Gson().fromJson(result, GenresDTO::class.java)
    }

    suspend fun getCompanies(): CompanyDTO {
        val result =
            apiCall("https://api.themoviedb.org/3/watch/providers/movie?language=de-DE&watch_region=DE")
        return Gson().fromJson(result, CompanyDTO::class.java)
    }

    suspend fun getMovieCredits(personId: Int): List<MovieInfo> {
        val movieCreditsJSON =
            apiCall("https://api.themoviedb.org/3/person/$personId/movie_credits?language=de-DE")
        val tvCreditsJSON =
            apiCall("https://api.themoviedb.org/3/person/$personId/tv_credits?language=de-DE")
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
                popularity = dto.popularity ?: 0,
                voteAverage = dto.vote_average ?: 0,
                voteCount = dto.vote_count ?: 0,
                posterPath = dto.poster_path ?: "",
                title = dto.title ?: dto.name,
                releaseDate = dto.release_date ?: dto.first_air_date ?: "",
                isMovie = !dto.release_date.isNullOrBlank()
            )
        }
    }

    suspend fun getVideo(movieInfo: MovieInfo): List<VideoInfoDTO>{
        val videoJSON = apiCall("https://api.themoviedb.org/3/${if(movieInfo.isMovie) "movie" else "tv"}/${movieInfo.id}/videos?language=de-DE")
        val video = Gson().fromJson(videoJSON, VideoDTO::class.java)
        return video.results
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



