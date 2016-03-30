package org.wonderbeat.dota.api.json

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonRootName

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("result")
data class MatchesPageJson(val status: Int,
                           val numResults: Int,
                           val totalResults: Int,
                           val resultsRemaining: Int,
                           val matches: List<MatchJson>)
