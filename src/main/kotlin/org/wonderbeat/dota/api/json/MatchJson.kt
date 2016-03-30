package org.wonderbeat.dota.api.json

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MatchJson(val matchId: Long,
                     val matchSeqNum: Long,
                     val startTime: Long,
                     val lobbyType: Int,
                     val radiantTeamId: Int,
                     val direTeamId: Int,
                     val players: List<MatchPlayerJson>)
