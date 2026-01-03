package com.example.tp.auth

import com.google.firebase.auth.FirebaseAuth

fun signInWithEmail(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        onError("Email and password are empty. Please introduce them again.")
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) onSuccess()
            else onError(task.exception?.localizedMessage ?: "Login failed")
        }
}

fun registerWithEmail(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (email.isBlank() || password.length < 6) {
        onError("Email cannot be empty and password must be at least 6 characters")
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) onSuccess()
            else onError(task.exception?.localizedMessage ?: "Registration failed")
        }
}
