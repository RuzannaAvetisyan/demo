package com.example.demo.dto

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
data class CountryStatDTO(
    val date: LocalDate,
    val country: String,
    val count: Long
)
