package org.wonderbeat.dota.api.json

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MatchPlayerJson(val accountId: Long,
                           val playerSlot: Int,
                           val heroId: Int)
