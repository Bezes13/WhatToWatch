package com.movies.whattowatch.dataClasses

data class Provider (
    val providerName: String,
    val providerId: Int,
    val logoPath: String,
    val priority: Int,
    val show: Boolean,
    val isUpdating: Boolean
): Comparable<Provider> {
    override fun compareTo(other: Provider): Int {
        return priority - other.priority
    }
}
