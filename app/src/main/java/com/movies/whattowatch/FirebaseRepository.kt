package com.movies.whattowatch


import com.movies.whattowatch.dataClasses.ProviderID
import com.movies.whattowatch.dataClasses.UserMovie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val collection = db.collection("collection")
    private val movieDocument = "Movies"
    private val providerDocument = "Provider"

    suspend fun addOrUpdateUserMovie(userMovie: UserMovie) {
        try {
            auth.uid?.let { collection.document(it).collection(movieDocument).document("${userMovie.movieId}").set(userMovie).await() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun addProvider(provider: Int) {
        try {
            auth.uid?.let { collection.document(it).collection(providerDocument).document(provider.toString()).set(ProviderID(provider)).await() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun removeProvider(provider: Int) {
        try {
            auth.uid?.let { collection.document(it).collection(providerDocument).document(provider.toString()).delete().await() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getProvider(): List<Int> {
        return try {
            val snapshot = auth.uid?.let {
                collection.document(it).collection(providerDocument)
                    .get()
                    .await()
            }
            (snapshot?.toObjects(ProviderID::class.java) ?: listOf()).map { it.providerID }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getUserMovies(): List<UserMovie> {
        return try {
            val snapshot = auth.uid?.let {
                collection.document(it).collection(movieDocument)
                    .get()
                    .await()
            }
            snapshot?.toObjects(UserMovie::class.java) ?: listOf()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}