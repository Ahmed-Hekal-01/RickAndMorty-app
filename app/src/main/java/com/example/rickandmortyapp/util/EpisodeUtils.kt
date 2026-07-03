package com.example.rickandmortyapp.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

fun openEpisodeInBrowser(
    context: Context,
    seasonNumber: Int,
    episodeNumber: Int
) {
    val url = buildEpisodeWatchUrl(
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber
    )

    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}

fun arabicSeasonName(seasonNumber: Int): String {
    return when (seasonNumber) {
        1 -> "الاول"
        2 -> "الثاني"
        3 -> "الثالث"
        4 -> "الرابع"
        5 -> "الخامس"
        6 -> "السادس"
        7 -> "السابع"
        8 -> "الثامن"
        9 -> "التاسع"
        10 -> "العاشر"
        else -> seasonNumber.toString()
    }
}

fun isLastEpisode(seasonNumber: Int, episodeNumber: Int): Boolean {
    return (seasonNumber == 1 && episodeNumber == 11) || (seasonNumber > 1 && episodeNumber == 10)
}

fun buildEpisodeWatchUrl(
    seasonNumber: Int,
    episodeNumber: Int
): String {
    val seasonArabicName = arabicSeasonName(seasonNumber)
    
    val suffix = if (isLastEpisode(seasonNumber, episodeNumber)) "مترجمة-والاخيرة" else "مترجمة"

    val slug =
        "مسلسل-rick-and-morty-الموسم-$seasonArabicName-الحلقة-$episodeNumber-$suffix"

    return "https://web.topcinemaa.com/${Uri.encode(slug, "-")}/"
}
