package com.example.demo.config

import com.example.demo.database.ClickhouseDatabase
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.yandex.clickhouse.ClickHouseDataSource
import ru.yandex.clickhouse.settings.ClickHouseProperties
import tanvd.aorm.Database

@Configuration
class BeanConfiguration {
    @Value("\${db-name}")
    lateinit var dbName: String
    @Value("\${url}")
    lateinit var url: String
    @Value("\${username}")
    lateinit var username: String
    @Value("\${password}")
    lateinit var password: String

    @Bean
    fun database(): ClickhouseDatabase {
        return ClickhouseDatabase.also {
            it.database = Database(dbName,
                ClickHouseDataSource(url, ClickHouseProperties().withCredentials(username, password)))
        }
    }
}