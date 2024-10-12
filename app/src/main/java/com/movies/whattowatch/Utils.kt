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

