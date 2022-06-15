package com.example.demo.database.tables

import com.example.demo.database.transaction
import tanvd.aorm.Engine
import tanvd.aorm.Table

object GeoDataTable: Table("geo_data1") {
    val date = date("date_col")
    val userId = string("user_id_col")
    val country = string("country_col")
    val ip = string("ip_col")

    override val engine: Engine = Engine.MergeTree(date, listOf(date, userId, country, ip))

    init {
        transaction {
            GeoDataTable.syncScheme()
        }
    }
}