package com.example.whattowatch.extension

fun String.getJustYear(): String {
    return this.substring(0,4)
}