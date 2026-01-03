package com.example.tp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


private const val TAG = "FirestoreExample"

class ExperimentViewModel : ViewModel() {

    private val db = Firebase.firestore

    fun addExperiment(
        title: String,
        objective: String,
        date: String,
        tags: List<String>,
        photos: List<String>,
        createdByUid: String
    ) {
        val experiment = mapOf(
            "title" to title,
            "objective" to objective,
            "date" to date,
            "tags" to tags,
            "photos" to photos,
            "createdByUid" to createdByUid
        )

        db.collection("experiments")
            .add(experiment)
            .addOnSuccessListener {
                Log.d(TAG, "Experiment added with ID: ${it.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding experiment", e)
            }
    }
}


