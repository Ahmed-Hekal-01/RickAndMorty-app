package com.example.rickandmortyapp.data.mapper

import com.example.rickandmortyapp.data.model.Episode
import com.example.rickandmortyapp.data.remote.dto.EpisodeDto

fun EpisodeDto.toDomain(): Episode {
    val seasonAndEpisode = parseSeasonAndEpisode(this.episode)
    return Episode(
        id = this.id,
        name = this.name,
        seasonNumber = seasonAndEpisode.first,
        episodeNumber = seasonAndEpisode.second,
        airDate = this.airDate,
        characterIds = this.characters.map { url -> url.substringAfterLast("/") }
    )
}

private fun parseSeasonAndEpisode(episodeString: String): Pair<Int, Int> {
    // "S01E01" -> season 1, episode 1
    val regex = "S(\\d+)E(\\d+)".toRegex()
    val matchResult = regex.find(episodeString)
    return if (matchResult != null) {
        val (season, episode) = matchResult.destructured
        Pair(season.toIntOrNull() ?: 0, episode.toIntOrNull() ?: 0)
    } else {
        Pair(0, 0)
    }
}
