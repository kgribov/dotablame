package org.wonderbeat

import com.google.common.collect.EvictingQueue
import org.joda.time.Duration


data class Blame(val descr: String)

interface BlameProcessor {
    fun process(entity: GameEntityFrame): Blame?
}

class BlameForWards(val team: Int): BlameProcessor {

    val boughtWardsTime = EvictingQueue.create<Duration>(2)

    override fun process(entity: GameEntityFrame): Blame? {
        entity.created.any { it is WardItem && it.team == team }.apply { boughtWardsTime.add(entity.timestamp) }
        return when {
            boughtWardsTime.size == 1 && boughtWardsTime.first() < Duration.standardMinutes(10) -> Blame("No wards were bought till 10 min")
            boughtWardsTime.size == 2 && (boughtWardsTime.take(2).reduceRight({a,b -> b.minus(a)}) > Duration.standardMinutes(10)) -> Blame("No wards bought for 10 mins")
            else -> null
        }
    }
}

class BlameForWardsNPC(val team: Int): BlameProcessor {

    var noWardsOnMap: Duration? = null

    override fun process(entity: GameEntityFrame): Blame? {
        val wardsOnMap = (entity.created + entity.updated).filter { it is WardNPC && it.teamNum == team }
        return when {
            wardsOnMap.isEmpty() && noWardsOnMap == null -> { noWardsOnMap = entity.timestamp; null}
            wardsOnMap.isEmpty() -> {
                val checkTime = entity.timestamp
                if(checkTime.minus(noWardsOnMap) > Duration.standardMinutes(10)) {
                    noWardsOnMap = null
                    Blame("No wards on map for 10 mins")
                } else {
                    null
                }
            }
            else -> { noWardsOnMap = null; null }
        }
    }
}

val blameForFastDeath = object: BlameProcessor {
    override fun process(entity: GameEntityFrame): Blame? =
            if(entity.timestamp < Duration.standardMinutes(1)) entity.updated.first { it is Hero && !it.alive }.let {
                Blame("Hero ${(it as Hero).name} died on the first minute")
            } else {
                null
            }
}
