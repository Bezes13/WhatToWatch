package com.movies.whattowatch.dto

data class MovieInfoDTO(
    val id: Int,
    val name: String = "",
    val original_language: String? = "",
    val original_name: String? = "",
    val character: String? = "",
    val overview: String?,
    val popularity: Double?,
    val poster_path: String?,
    val release_date: String?,
    val first_air_date: String?,
    val title: String?,
    val vote_average: Double?,
    val video: Boolean?,
    val vote_count: Int?,
    val media_type: String?,
    val known_for: List<MovieInfoDTO>?,
    val profile_path: String?
):Comparable<MovieInfoDTO> {
    override fun compareTo(other: MovieInfoDTO): Int {
        if(popularity== null){
            return 1
        }
        if (other.popularity == null){
            return -1
        }
        return other.popularity.toInt() - popularity.toInt()
    }
}
