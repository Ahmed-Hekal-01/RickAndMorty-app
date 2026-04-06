package com.example.rickandmortyapp.data.mapper

import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.dto.IPageResponse

fun <T, R> IPageResponse<T>.toPage(mapper: (T) -> R): Page<R> {
    return Page(
        count = this.info.count,
        pages = this.info.pages,
        next = this.info.next,
        prev = this.info.prev,
        results = this.result.map(mapper)
    )
}

