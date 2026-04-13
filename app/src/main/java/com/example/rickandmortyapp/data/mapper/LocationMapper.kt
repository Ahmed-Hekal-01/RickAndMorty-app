package com.example.rickandmortyapp.data.mapper

import com.example.rickandmortyapp.data.model.Location
import com.example.rickandmortyapp.data.remote.dto.LocationDto
import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.dto.LocationPageResponse


fun LocationDto.toDomain(): Location {
    return Location(
        id = this.id,
        name = this.name,
        type = this.type,
        dimension = this.dimension,
        residentCharactersIds = this.residentCharacters.map{ it.substringAfterLast("/")}
    )
}