package com.example.demo.database.entities

import com.example.demo.database.tables.GeoDataTable
import com.example.demo.dto.CountryStatDTO
import kotlinx.datetime.LocalDate
import tanvd.aorm.SelectRow
import tanvd.aorm.expression.count
import java.util.*


data class GeoDataEntity(
    val date: Date,
    val userId: String,
    val country: String,
    val ip: String
)

fun SelectRow.toGeoDataEntity() = GeoDataEntity(this[GeoDataTable.date], this[GeoDataTable.userId],
    this[GeoDataTable.country], this[GeoDataTable.ip])
fun SelectRow.toCountryStatDTO() = CountryStatDTO(LocalDate.parse(this[GeoDataTable.date].toString()),
    this[GeoDataTable.country], this[count(GeoDataTable.date)])
