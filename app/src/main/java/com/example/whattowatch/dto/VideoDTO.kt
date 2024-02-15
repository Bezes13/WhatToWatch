package com.example.whattowatch.dto

data class VideoDTO(
    val results: List<VideoInfoDTO>
)

data class VideoInfoDTO(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String
)
