package com.example.whattowatch.extension

fun String.getJustYear(): String {
    if (this.length>=4){
        return this.substring(0,4)
    }
    return this
}