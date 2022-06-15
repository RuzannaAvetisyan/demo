package com.example.demo

import com.example.demo.database.ClickhouseDatabase
import com.example.demo.database.tables.GeoDataTable
import com.example.demo.database.transaction
import com.example.demo.dto.GeoDataDTO
import com.example.demo.service.GeoDataService
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlin.test.BeforeTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import tanvd.aorm.implementation.TableClickhouse
import kotlin.test.assertEquals


@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests(@Autowired val mockMvc: MockMvc, @Autowired val geoDataService: GeoDataService) {

	@Test
	fun contextLoads() {
	}

	@Test
	fun testGeoData(){
		geoDataService.insert(userId, timestamp, country, ip)
		this.mockMvc.perform(get("/countrystats").param(start.first, start.second)
			.param(end.first, end.second).contentType(MediaType.APPLICATION_JSON)).andReturn().also {
			assertEquals(it.response.status, HttpStatus.OK.value())
		}
		this.mockMvc.perform(get("/countrystats").param(end.first, start.second)
			.param(start.first, end.second).contentType(MediaType.APPLICATION_JSON)).andReturn().also {
			assertEquals(it.response.status, HttpStatus.NO_CONTENT.value())
		}
		this.mockMvc.perform(post("/geodata").contentType(MediaType.APPLICATION_JSON)
			.content(geoDataDTOString)).andReturn().also {
			assertEquals(it.response.status, HttpStatus.OK.value())
		}
	}

	@BeforeTest
	fun before(){
		transaction {
			TableClickhouse.drop(ClickhouseDatabase.database, GeoDataTable)
			GeoDataTable.syncScheme()
		}
	}

	companion object {
		val geoDataDTOString = Json.encodeToString(GeoDataDTO("AM", 1607417359, "askgjasassa"))
		const val userId = "askgjasassa"
		const val timestamp = 16074173590
		const val country = "GB"
		const val ip = "0.0.0.0"
		val start = "startDate" to "1970-01-01"
		val end = "endDate" to "2099-01-01"
	}
}
