package org.wonderbeat.dota.api.json

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MatchAbilityUpgradeJson(val ability: Int,
                                   val time: Long,
                                   val level: Int)
