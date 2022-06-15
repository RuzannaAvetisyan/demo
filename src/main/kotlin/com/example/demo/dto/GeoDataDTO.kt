package com.example.demo.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeoDataDTO(
    val country: String,
    val timestamp: Long,
    val userId: String,
)