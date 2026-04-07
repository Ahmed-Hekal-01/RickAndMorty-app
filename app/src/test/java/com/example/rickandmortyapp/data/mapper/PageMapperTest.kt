package com.example.rickandmortyapp.data.mapper

import com.example.rickandmortyapp.data.remote.dto.IPageResponse
import com.example.rickandmortyapp.data.remote.dto.Info
import org.junit.Assert.assertEquals
import org.junit.Test

class PageMapperTest {

    @Test
    fun toPage_mapsInfoAndTransformsResults() {
        val response = FakePageResponse(
            info = Info(count = 42, pages = 5, next = "next-url", prev = null),
            results = listOf(1, 2, 3)
        )

        val page = response.toPage { value -> "item-$value" }

        assertEquals(42, page.count)
        assertEquals(5, page.pages)
        assertEquals("next-url", page.next)
        assertEquals(null, page.prev)
        assertEquals(listOf("item-1", "item-2", "item-3"), page.results)
    }

    private data class FakePageResponse(
        override val results: List<Int>,
        override val info: Info
    ) : IPageResponse<Int>
}

