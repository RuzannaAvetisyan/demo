package com.example.demo.database

import tanvd.aorm.Database
import tanvd.aorm.context.ConnectionContext
import tanvd.aorm.withDatabase

object ClickhouseDatabase {
    lateinit var database: Database
}

fun <T> transaction(statement: ConnectionContext.() -> T): T = withDatabase(ClickhouseDatabase.database) {
    statement()
}
