package com.example.rickandmortyapp.data.remote.dto

interface IPageResponse<T> {
    val result: List<T>
    val info: Info
}
