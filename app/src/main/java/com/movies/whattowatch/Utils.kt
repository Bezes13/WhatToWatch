package com.movies.whattowatch


import android.os.Build
import java.lang.Exception
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

fun yearsSince(dateString: String): Int {
    val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    } else {
        throw Exception()
    }
    val pastDate = LocalDate.parse(dateString, formatter)
    val currentDate = LocalDate.now()

    return Period.between(pastDate, currentDate).years
}

fun formatDateString(dateString: String): String {
    // Define the date format that matches the input string
    val inputFormatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    } else {
        throw Exception()
    }

    // Parse the input string into a LocalDate
    val date = LocalDate.parse(dateString, inputFormatter)

    // Define the desired output format with localized month
    val outputFormatter = DateTimeFormatter.ofPattern("dd. MMM yyyy", Locale.GERMAN)

    // Format the date into the desired output format
    return date.format(outputFormatter)
}

fun Int.getRevenue(): String{
    val billion = 1000000000
    if(this >= billion){
        return "${this.div(billion)} Billion $"
    }
    val million = 1000000
    if(this >= million){
        return "${this.div(million)} Million $"
    }
    val thousand = 1000
    if(this >= thousand){
        return "${this.div(thousand)} K $"
    }
    return "$this$"
}

