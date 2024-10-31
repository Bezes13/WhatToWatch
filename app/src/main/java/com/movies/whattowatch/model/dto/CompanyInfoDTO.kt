package com.movies.whattowatch.model.dto

data class CompanyInfoDTO(
    val provider_name: String,
    val provider_id: Int,
    val logo_path: String?,
    val display_priorities: Map<String, Int>,
    val show: Boolean = true
): Comparable<CompanyInfoDTO> {
    override fun compareTo(other: CompanyInfoDTO): Int {
        return (display_priorities["DE"]?:0) - (other.display_priorities["DE"]?:0)
    }
}