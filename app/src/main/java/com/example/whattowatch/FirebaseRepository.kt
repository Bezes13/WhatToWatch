package com.example.whattowatch


import com.example.whattowatch.dataClasses.UserMovie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val movieList = db.collection("userMovie")
    private val provider = db.collection("provider")

    // TODO
    // Staffel und Folgen adden
    // Ansich generell größer
    // Seiten Ding anpassen
    // Alles über databse
    suspend fun addOrUpdateUserMovie(userMovie: UserMovie) {
        try {
            movieList.document("${userMovie.movieId}").set(userMovie).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun addProvider(provider: Int) {
        try {
            movieList.document(provider.toString()).set(provider).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun removeProvider(provider: Int) {
        try {
            movieList.document(provider.toString()).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getProvider(): List<Int> {
        return try {
            val snapshot = provider
                .get()
                .await()

            snapshot.toObjects(Int::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getUserMovies(): List<UserMovie> {
        return try {
            val snapshot = movieList
                .get()
                .await()

            snapshot.toObjects(UserMovie::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}