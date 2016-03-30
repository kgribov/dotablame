package org.wonderbeat.dota.api.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy

import java.io.IOException

import com.fasterxml.jackson.module.kotlin.*

class JsonParser {

    private val mapper = jacksonObjectMapper()
            .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)

    @Throws(IOException::class)
    fun <T> parse(value: String, valueType: Class<T>): T =
        mapper.readValue(value, valueType)

}
