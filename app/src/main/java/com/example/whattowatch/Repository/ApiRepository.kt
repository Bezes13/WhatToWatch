package com.example.whattowatch.Repository

import android.content.Context
import com.example.whattowatch.Data.Company
import com.example.whattowatch.Data.CompanyInfo
import com.example.whattowatch.Data.Credits
import com.example.whattowatch.Data.Genre
import com.example.whattowatch.Data.GenreDTO
import com.example.whattowatch.Data.MovieAvailability
import com.example.whattowatch.Data.MovieDTO
import com.example.whattowatch.Data.MovieInfo
import com.example.whattowatch.R
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Properties

class ApiRepository(private val context: Context) {

    private val client = OkHttpClient()

    suspend fun getMovies(page: Int, genre: Genre, companies: List<CompanyInfo>): MovieDTO {
        val result =
            apiCall("https://api.themoviedb.org/3/discover/movie?include_adult=true&include_video=false&language=en-US&page=$page&sort_by=popularity.desc&watch_region=DE&with_genres=${genre.id}&with_watch_providers=${
                companies.joinToString("|") { it.provider_id.toString() }
            }")
        return Gson().fromJson(result, MovieDTO::class.java)
    }

    suspend fun getMovieDetails(movieId: Int): MovieInfo {
        val result = apiCall("https://api.themoviedb.org/3/movie/$movieId?language=en-US")
        return Gson().fromJson(result, MovieInfo::class.java)
    }

    suspend fun getCast(movieId: Int): List<String> {
        val result = apiCall("https://api.themoviedb.org/3/movie/$movieId/credits?language=en-US")
        val credits = Gson().fromJson(result, Credits::class.java)
        return credits.cast.map { member -> member.name }.filterIndexed { index, _ -> index < 5 }
    }

    suspend fun getProviders(movieId: Int): MovieAvailability {
        val result = apiCall("https://api.themoviedb.org/3/movie/$movieId/watch/providers")
        return Gson().fromJson(result, MovieAvailability::class.java)

    }

    suspend fun getGenres(): GenreDTO {
        val result = apiCall("https://api.themoviedb.org/3/genre/movie/list?language=de")
        return Gson().fromJson(result, GenreDTO::class.java)
    }

    suspend fun getCompanies(): Company {
        val result =
            apiCall("https://api.themoviedb.org/3/watch/providers/movie?language=en-US&watch_region=DE")
        return Gson().fromJson(result, Company::class.java)

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



