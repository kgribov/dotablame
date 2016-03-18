package org.wonderbeat

import org.junit.Test
import skadistats.clarity.Clarity
import skadistats.clarity.model.Entity
import skadistats.clarity.source.InputStreamSource
import java.io.ByteArrayInputStream
import java.util.*
import java.util.stream.StreamSupport


class ClarityTest {
    fun <T> T?.orElse(default: () -> T?): T? = if(this != null) this else default()

    @Test fun testClarity() {
        val info = Clarity.infoForStream(ByteArrayInputStream(ClassLoader.getSystemResourceAsStream("2212949307.dem").readBytes()))

        val game = ClassLoader.getSystemResourceAsStream("2212949307.dem").buffered()
        val frameSource = FrameSource(InputStreamSource(game))
        val wardBlame = BlameForWards(2)
        val wardNpcBlame = BlameForWardsNPC(2)
        StreamSupport.stream(Spliterators.spliterator(frameSource.iterator(), info.playbackTicks.toLong(), Spliterator.NONNULL.or(Spliterator.DISTINCT)), false)
                .map { it.toTimestamped() }
                .filter { it.timestamp != null }
                .map {
                    fun List<Entity>.extract(): List<GameEntity> = this.map { it.extractWardItem().orElse { it.extractWardNPC() }.orElse { it.extractHero() } }.filterNotNull()
                    GameEntityFrame(it.timestamp!!, it.frame.created.extract(), it.frame.updated.extract(), it.frame.deleted.extract()) }
                .filter { it.created.isNotEmpty() || it.updated.isNotEmpty() || it.deleted.isNotEmpty() }
                .map { listOf(wardBlame.process(it), wardNpcBlame.process(it), blameForFastDeath.process(it)) }
                .forEach { it.filter { it != null }.map { println(it!!.descr)} }
    }


}

