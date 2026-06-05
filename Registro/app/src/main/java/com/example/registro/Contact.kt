package com.example.registro

data class Contact(
    val id: String,
    val name: String,
    val phone: String,
    val isFavorite: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)