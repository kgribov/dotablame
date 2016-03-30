package org.wonderbeat.dota.api.http

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

import java.io.IOException
import java.util.concurrent.TimeUnit

class OkHttpExecutor {

    private val TIMEOUT_CONNECT_IN_SECONDS = 120
    private val TIMEOUT_READ_IN_SECONDS = 120
    private val TIMEOUT_WRITE_IN_SECONDS = 120

    private val client: OkHttpClient

    init {
        this.client = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_CONNECT_IN_SECONDS.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ_IN_SECONDS.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_WRITE_IN_SECONDS.toLong(), TimeUnit.SECONDS)
                .build()
    }

    @Throws(IOException::class)
    fun execute(url: HttpUrl): String {
        val request = Request.Builder().url(url).build()
        return client.newCall(request).execute().body().string()
    }
}
