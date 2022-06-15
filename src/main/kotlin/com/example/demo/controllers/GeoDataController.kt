package com.example.demo.controllers

import com.example.demo.dto.CountryStatDTO
import com.example.demo.dto.GeoDataDTO
import com.example.demo.service.GeoDataService
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletRequest

@RestController
class GeoDataController(
    @Value("\${headers-ip}") val headers: Array<String>,
    @Autowired val geoDataService: GeoDataService
){
    private val format = SimpleDateFormat("yyyy-MM-dd")

    @PostMapping("/geodata")
    fun geoData(request: HttpServletRequest, @RequestBody geoDataString: String): ResponseEntity<*> {
        return try{
            val requestIP = getRequestIP(request)
            val geoDataDTO = Json.decodeFromString<GeoDataDTO>(geoDataString)
            geoDataService.newGeoData(geoDataDTO.userId, geoDataDTO.timestamp, geoDataDTO.country, requestIP)
            ResponseEntity.ok("OK")
        }catch (e: Exception){
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/countrystats")
    fun countryStats(
        @RequestParam(name = "startDate", required = true) startDate: String,
        @RequestParam(name = "endDate", required = true) endDate: String,
        @RequestParam(name = "groupLocal", required = true, defaultValue = "false") groupLocal: Boolean ,
    ): ResponseEntity<*> {
        val stat = geoDataService.getCountryStats(format.parse(startDate), format.parse(endDate), groupLocal)
        return try {
            if(stat.isEmpty()){
                ResponseEntity.status(HttpStatus.NO_CONTENT).body(emptyList<CountryStatDTO>())
            }else {
                ResponseEntity.status(HttpStatus.OK).body(Json.encodeToString(stat))
            }
        }catch (e: Exception){
            ResponseEntity.badRequest().body(e.message)
        }
    }

    private fun getRequestIP(request : HttpServletRequest): String{
        for (header in headers){
            val result = request.getHeader(header)
            if (!result.isNullOrBlank()) {
                return result.split(",").first()
            }
        }
        return request.remoteAddr
    }
}