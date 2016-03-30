package org.wonderbeat.dota.api

import com.google.common.collect.Iterables
import org.wonderbeat.dota.api.json.MatchDetailsJson
import org.wonderbeat.dota.api.json.MatchJson
import org.wonderbeat.dota.api.service.MatchDetailsService
import org.wonderbeat.dota.api.service.MatchService
import reactor.Environment
import reactor.rx.Stream
import reactor.rx.Streams

class MatchesStreamSource(private val matchService: MatchService, private val matchDetailsService: MatchDetailsService) {

    fun getAccountMatchesStream(steamAccountId: Long): Stream<MatchJson> {
        Environment.initializeIfEmpty()

        return Streams.from(MatchesIterable(matchService, steamAccountId))
    }

    fun getAccountMatchDetailsStream(steamAccountId: Long): Stream<MatchDetailsJson> {
        return getAccountMatchesStream(steamAccountId)
                .map({match -> matchDetailsService.getMatchDetails(match.matchId)})
                .throttle(1000)
    }
}

private class MatchesIterable(val matchService: MatchService,
                              val steamAccountId: Long) : MutableIterable<MatchJson> {

    override fun iterator(): MutableIterator<MatchJson> {
        return object : MutableIterator<MatchJson> {

            var sequenceId: Long = 0L
            var index: Int = 0
            var matches = emptyList<MatchJson>()

            override fun hasNext(): Boolean {
                return true
            }

            override fun next(): MatchJson {
                if (index === matches.size) {

                    if (!matches.isEmpty()) {
                        sequenceId = Iterables.getLast(matches).matchSeqNum
                    }
                    matches = matchService.getMatchesSince(steamAccountId, sequenceId)
                    index = 0
                }

                return matches[index++]
            }

            override fun remove() {
                throw UnsupportedOperationException()
            }
        }
    }
}
