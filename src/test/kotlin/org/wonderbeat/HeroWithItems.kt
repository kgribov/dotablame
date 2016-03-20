package org.wonderbeat

import org.junit.Test
import skadistats.clarity.Clarity
import skadistats.clarity.model.Entity
import skadistats.clarity.source.InputStreamSource
import java.io.ByteArrayInputStream
import java.util.*
import java.util.stream.StreamSupport

class HeroWithItems {

    fun <T> T?.orElse(default: () -> T?): T? = if (this != null) this else default()

    @Test
    fun assembleHero() {
        val info = Clarity.infoForStream(ByteArrayInputStream(ClassLoader.getSystemResourceAsStream("2212949307.dem").readBytes()))

        val game = ClassLoader.getSystemResourceAsStream("2212949307.dem").buffered()
        val frameSource = FrameSource(InputStreamSource(game))
        val heroassebmle = HeroAssembler()
        StreamSupport.stream(Spliterators.spliterator(frameSource.iterator(), info.playbackTicks.toLong(), Spliterator.NONNULL.or(Spliterator.DISTINCT)), false)
                .skip(50000)
                .limit(2000)
                .map { it.toTimestamped() }
                .filter { it.timestamp != null }
                .map {
                    fun List<Entity>.extract(): List<GameEntity> = this.map { it.extractHero().orElse { it.extractItem() } }.filterNotNull()
                    GameEntityFrame(it.timestamp!!, it.frame.created.extract(), it.frame.updated.extract(), it.frame.deleted.extract())
                }
                .map { heroassebmle.assemble(it) }
                .forEach {
                    it.updated.filterIsInstance<HeroWithInventory>().forEach {
                        println(it)
                    }
                }
    }
}

