package com.example.demo.service

import com.example.demo.database.entities.GeoDataEntity
import com.example.demo.database.entities.toCountryStatDTO
import com.example.demo.database.entities.toGeoDataEntity
import com.example.demo.database.tables.GeoDataTable
import com.example.demo.database.transaction
import com.example.demo.dto.CountryStatDTO
import kotlinx.datetime.LocalDate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import tanvd.aorm.expression.count
import tanvd.aorm.query.between
import tanvd.aorm.query.eq
import tanvd.aorm.query.groupBy
import tanvd.aorm.query.where
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.LinkedBlockingQueue

@Service(value = "GeoDataService")
class GeoDataServiceImpl: GeoDataService {

    private val batch = ConcurrentLinkedQueue<GeoDataEntity>()

    override fun newGeoData(userId: String, timestamp: Long, country: String, ip: String) {
        batch.add(GeoDataEntity(Date(timestamp), userId, country, ip))
    }

    @Scheduled(fixedDelay = 60000)
    private fun batchInsert(){
        val list = listOf<GeoDataEntity>(*batch.toTypedArray())
        if(list.isNotEmpty()){
            batch.clear()
            transaction{
                GeoDataTable.batchInsert(list){ table, value ->
                    table[GeoDataTable.date] = value.date
                    table[GeoDataTable.userId] = value.userId
                    table[GeoDataTable.country] = value.country
                    table[GeoDataTable.ip] = value.ip
                }
            }
        }
    }

    override fun insert(userId: String, timestamp: Long, country: String, ip: String){
        transaction{
            GeoDataTable.insert{
                it[GeoDataTable.date] = Date(timestamp)
                it[GeoDataTable.userId] = userId
                it[GeoDataTable.country] = country
                it[GeoDataTable.ip] = ip
            }
        }
    }

    override fun getCountryStats(startDate: Date, endDate: Date, groupLocal: Boolean): List<CountryStatDTO> {
        return if(groupLocal){
            transaction{
                val query = GeoDataTable.select() where GeoDataTable.date.between(startDate to endDate)
                query.toResult().map { it.toGeoDataEntity() }.groupingBy{ it.date to it.country }.eachCount()
                    .map { CountryStatDTO(LocalDate.parse(it.key.first.toString()), it.key.second, it.value.toLong()) }
            }
        }else {
            transaction{
                GeoDataTable.select(GeoDataTable.date, GeoDataTable.country, count(GeoDataTable.date))
                    .groupBy(GeoDataTable.date, GeoDataTable.country)
                    .where(GeoDataTable.date.between(startDate to endDate))
                    .toResult().map { it.toCountryStatDTO() }
            }
        }
    }
}