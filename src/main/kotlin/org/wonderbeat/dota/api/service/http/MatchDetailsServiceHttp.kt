package org.wonderbeat.dota.api.service.http

import org.wonderbeat.dota.api.http.SteamHttpParams
import okhttp3.HttpUrl
import org.wonderbeat.dota.api.SteamConfig
import org.wonderbeat.dota.api.http.JsonHttpExecutor
import org.wonderbeat.dota.api.json.MatchDetailsJson
import org.wonderbeat.dota.api.service.MatchDetailsService

class MatchDetailsServiceHttp(private val steamConfig: SteamConfig,
                              private val jsonHttpExecutor: JsonHttpExecutor) : MatchDetailsService {

    override fun getMatchDetails(matchId: Long?): MatchDetailsJson {
        val url = HttpUrl.Builder()
                .host(SteamHttpParams.STEAM_HOST)
                .scheme(SteamHttpParams.STEAM_SCHEMA)
                .addPathSegment(SteamHttpParams.MATCH_API_PATH)
                .addPathSegment(SteamHttpParams.GET_MATCH_DETAILS_PATH)
                .addPathSegment(SteamHttpParams.STEAM_API_VERSION_PATH)
                .setQueryParameter(SteamHttpParams.MATCH_ID_PARAM, matchId.toString())
                .setQueryParameter(SteamHttpParams.DEV_KEY_PARAM, steamConfig.devKey)
                .build()

        val matchDetails = jsonHttpExecutor.execute(url, MatchDetailsJson::class.java)

        return matchDetails
    }
}
