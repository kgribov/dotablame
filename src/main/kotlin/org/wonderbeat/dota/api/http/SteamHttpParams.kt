package org.wonderbeat.dota.api.http

object SteamHttpParams {

    val STEAM_SCHEMA = "https"
    val STEAM_HOST = "api.steampowered.com"

    val MATCH_API_PATH = "IDOTA2Match_570"

    val GET_MATCH_HISTORY_PATH = "GetMatchHistory"
    val GET_MATCH_DETAILS_PATH = "GetMatchDetails"

    val STEAM_API_VERSION_PATH = "V001"

    val START_MATCH_SEQ_PARAM = "start_at_match_seq_num"
    val STEAM_ACCOUNT_ID_PARAM = "account_id"
    val MATCH_ID_PARAM = "match_id"
    val DEV_KEY_PARAM = "key"
}
