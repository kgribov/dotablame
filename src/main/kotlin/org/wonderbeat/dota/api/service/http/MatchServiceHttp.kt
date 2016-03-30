package org.wonderbeat.dota.api.service.http

import org.wonderbeat.dota.api.http.SteamHttpParams
import okhttp3.HttpUrl
import org.wonderbeat.dota.api.SteamConfig
import org.wonderbeat.dota.api.http.JsonHttpExecutor
import org.wonderbeat.dota.api.json.MatchJson
import org.wonderbeat.dota.api.json.MatchesPageJson
import org.wonderbeat.dota.api.service.MatchService

class MatchServiceHttp(private val steamConfig: SteamConfig,
                       private val jsonHttpExecutor: JsonHttpExecutor) : MatchService {

    override fun getMatchesSince(accountId: Long?, sequenceId: Long?): List<MatchJson> {
        val url = HttpUrl.Builder()
                .host(SteamHttpParams.STEAM_HOST)
                .scheme(SteamHttpParams.STEAM_SCHEMA)
                .addPathSegment(SteamHttpParams.MATCH_API_PATH)
                .addPathSegment(SteamHttpParams.GET_MATCH_HISTORY_PATH)
                .addPathSegment(SteamHttpParams.STEAM_API_VERSION_PATH)
                .setQueryParameter(SteamHttpParams.START_MATCH_SEQ_PARAM, sequenceId.toString())
                .setQueryParameter(SteamHttpParams.STEAM_ACCOUNT_ID_PARAM, accountId.toString())
                .setQueryParameter(SteamHttpParams.DEV_KEY_PARAM, steamConfig.devKey)
                .build()

        return jsonHttpExecutor.execute(url, MatchesPageJson::class.java).matches
    }
}
