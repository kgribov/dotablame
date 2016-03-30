package org.wonderbeat.dota.api.http

import com.github.rholder.retry.*
import okhttp3.HttpUrl
import org.slf4j.LoggerFactory
import org.wonderbeat.dota.api.json.JsonParser

import java.io.IOException
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class JsonHttpExecutor(private val jsonParser: JsonParser,
                       private val httpExecutor: OkHttpExecutor) {

    fun <T> execute(httpUrl: HttpUrl, valueType: Class<T>): T {

        val retryer = RetryerBuilder.newBuilder<T>()
                .withStopStrategy(StopStrategies.stopAfterAttempt(10))
                .withWaitStrategy(WaitStrategies.fixedWait(5, TimeUnit.SECONDS))
                .build();

        return retryer.call(Callable<T> {
            val response = httpExecutor.execute(httpUrl)

            try {
                return@Callable jsonParser.parse(response, valueType)

            } catch (ex: IOException) {
                logger.error("Unable to create $valueType from response $response", ex)
                throw ex
            }
        })
     }



    companion object {
        private val logger = LoggerFactory.getLogger(JsonHttpExecutor::class.java)
    }
}
