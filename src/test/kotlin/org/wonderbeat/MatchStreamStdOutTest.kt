package org.wonderbeat

import org.junit.Test
import org.wonderbeat.dota.api.MatchesStreamSource
import org.wonderbeat.dota.api.SteamConfig
import org.wonderbeat.dota.api.http.JsonHttpExecutor
import org.wonderbeat.dota.api.http.OkHttpExecutor
import org.wonderbeat.dota.api.json.JsonParser
import org.wonderbeat.dota.api.service.http.MatchDetailsServiceHttp
import org.wonderbeat.dota.api.service.http.MatchServiceHttp

class MatchStreamStdOutTest {

    val devKey = "put your dev key here"

    @Throws(InterruptedException::class)
    @Test fun printMatchesForWonderbeatAccount() {

        val steamConfig = SteamConfig(devKey)
        val jsonHttpExecutor = JsonHttpExecutor(JsonParser(), OkHttpExecutor())

        val matchService = MatchServiceHttp(steamConfig, jsonHttpExecutor)
        val matchDetailsService = MatchDetailsServiceHttp(steamConfig, jsonHttpExecutor)

        val matchesStreamSource = MatchesStreamSource(matchService, matchDetailsService)

        matchesStreamSource.getAccountMatchDetailsStream(107656080)
                .consume { match -> println(match) }

        Thread.sleep(1000000)
    }
}
