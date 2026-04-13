package com.example.rickandmortyapp.data.model

data class Page<T>(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?,
    val results: List<T>
)
