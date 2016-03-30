package org.wonderbeat.dota.api.json

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class MatchPlayerDetailsJson(val accountId: Long,
                                  val playerSlot: Int,
                                  val heroId: Int,

                                  @JsonProperty("item_0")
                                  val item0: Int,
                                  @JsonProperty("item_1")
                                  val item1: Int,
                                  @JsonProperty("item_2")
                                  val item2: Int,
                                  @JsonProperty("item_3")
                                  val item3: Int,
                                  @JsonProperty("item_4")
                                  val item4: Int,
                                  @JsonProperty("item_5")
                                  val item5: Int,

                                  val kills: Int,
                                  val deaths: Int,
                                  val assists: Int,
                                  val leaverStatus: Int,
                                  val lastHits: Int,
                                  val denies: Int,
                                  val goldPerMin: Int,
                                  val xpPerMin: Int,
                                  val level: Int,
                                  val gold: Int,
                                  val goldSpent: Int,
                                  val heroDamage: Int,
                                  val heroHealing: Int,
                                  val abilityUpgrades: List<MatchAbilityUpgradeJson>)
