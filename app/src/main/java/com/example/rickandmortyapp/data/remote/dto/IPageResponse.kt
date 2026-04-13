package com.example.rickandmortyapp.data.remote.dto

interface IPageResponse<T> {
    val results: List<T>
    val info: Info
}
