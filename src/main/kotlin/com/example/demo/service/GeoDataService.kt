package com.example.demo.service

import com.example.demo.dto.CountryStatDTO
import java.util.*

interface GeoDataService {
    fun newGeoData(userId: String, timestamp: Long, country: String, ip: String)
    fun insert(userId: String, timestamp: Long, country: String, ip: String)
    fun getCountryStats(startDate: Date, endDate: Date, groupLocal: Boolean): List<CountryStatDTO>
}