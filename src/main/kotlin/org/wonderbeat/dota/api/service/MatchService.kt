package org.wonderbeat.dota.api.service

import org.wonderbeat.dota.api.json.MatchJson

interface MatchService {

    fun getMatchesSince(accountId: Long?, matchSeq: Long?): List<MatchJson>
}
