package org.wonderbeat.dota.api.service

import org.wonderbeat.dota.api.json.MatchDetailsJson

interface MatchDetailsService {

    fun getMatchDetails(matchId: Long?): MatchDetailsJson
}
