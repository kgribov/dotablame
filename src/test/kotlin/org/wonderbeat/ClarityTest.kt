package org.wonderbeat

import org.joda.time.format.PeriodFormatterBuilder
import org.junit.Test
import org.slf4j.LoggerFactory
import skadistats.clarity.Clarity
import skadistats.clarity.model.Entity
import skadistats.clarity.model.FieldPath
import skadistats.clarity.processor.entities.OnEntityCreated
import skadistats.clarity.processor.entities.OnEntityUpdated
import skadistats.clarity.processor.entities.UsesEntities
import skadistats.clarity.processor.runner.Context
import skadistats.clarity.source.InputStreamSource
import java.io.ByteArrayInputStream
import java.util.*
import java.util.stream.StreamSupport


class ClarityTest {


    @UsesEntities
    class TimeProcessor() {

        private val logger = LoggerFactory.getLogger("org.wonderbeat.dotablame")

        @OnEntityUpdated
        fun onCreated(ctx: Context, e: Entity, updatedPaths: Array<FieldPath>, updateCount: Int) {
            val updatedWardsPath = e.dtClass.collectFieldPaths(e.state).forEach {
                if(e.dtClass.getNameForFieldPath(it).contains("m_fGameTime")) {
                    logger.debug("time: ${e.getProperty<Float>("m_pGameRules.m_fGameTime")}")
                }
            }
        }

    }

    @UsesEntities
    class GameStartedProcessor() {

        private val logger = LoggerFactory.getLogger("org.wonderbeat.dotablame")

        fun Entity.isRune(): Boolean = this.getDtClass().getDtName().contains("Rune");

        private var isStarted = false

        @OnEntityCreated
        fun onCreated(ctx: Context, e: Entity) {
            if(e.isRune() && !isStarted ) {
                logger.info("Game started at ${ctx.tick}")
                isStarted = true
            }
        }

    }

    val GAMETIME_FORMATTER = PeriodFormatterBuilder()
            .minimumPrintedDigits(2)
            .printZeroAlways()
            .appendHours()
            .appendLiteral(":")
            .appendMinutes()
            .appendLiteral(":")
            .appendSeconds()
            .appendLiteral(".")
            .appendMillis3Digit()
            .toFormatter();

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

