package org.wonderbeat.dota.api.json

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonRootName

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("result")
data class MatchDetailsJson(val players: List<MatchPlayerDetailsJson>,
                            val radiantWin: Boolean,
                            val duration: Int,
                            val startTime: Long,
                            val matchId: Long,
                            val matchSeqNum: Long,
                            val towerStatusRadiant: Int,
                            val towerStatusDire: Int,
                            val barracksStatusRadiant: Int,
                            val barracksStatusDire: Int,
                            val cluster: Int,
                            val firstBloodTime: Long,
                            val lobbyType: Int,
                            val humanPlayers: Int,
                            val leagueid: Int,
                            val positiveVotes: Int,
                            val negativeVotes: Int,
                            val gameMode: Int,
                            val flags: Int,
                            val engine: Int)
