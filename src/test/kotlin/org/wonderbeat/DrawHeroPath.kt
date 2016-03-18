package org.wonderbeat

import org.junit.Test
import skadistats.clarity.Clarity
import skadistats.clarity.model.Entity
import skadistats.clarity.source.InputStreamSource
import java.awt.Color
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*
import java.util.stream.StreamSupport
import javax.imageio.ImageIO

class DrawHeroPath {

    @Test
    fun drawPath() {
        val info = Clarity.infoForStream(ByteArrayInputStream(ClassLoader.getSystemResourceAsStream("2212949307.dem").readBytes()))

        val game = ClassLoader.getSystemResourceAsStream("2212949307.dem").buffered()
        val frameSource = FrameSource(InputStreamSource(game))
        val image = BufferedImage(600, 600, BufferedImage.TYPE_4BYTE_ABGR)
        val graphics = image.createGraphics();
        graphics.background = Color.WHITE
        // update graphics object with the inverted y-transform
        graphics.translate(0.toDouble(), 599.toDouble());
        graphics.scale(1.toDouble(), -1.toDouble());

        StreamSupport.stream(Spliterators.spliterator(frameSource.iterator(), info.playbackTicks.toLong(), Spliterator.NONNULL.or(Spliterator.DISTINCT)), false)
                .map { it.toTimestamped() }
                .filter { it.timestamp != null && it!!.timestamp!!.millis % 5 == 0L }
                .filter { it.frame.updated.isNotEmpty() }
                .map {
                    fun List<Entity>.extract(): List<GameEntity> = this.map { it.extractHero() }.filterNotNull()
                    GameEntityFrame(it.timestamp!!, it.frame.created.extract(), it.frame.updated.extract(), it.frame.deleted.extract())
                }
                .map { it.updated }
                .forEach {
                    it.filter { (it as Hero).team == 2 }.forEach {
                        graphics.color = Color(0,0,0,50)

                        graphics.draw(Rectangle(((it as Hero).coord.x * 2).toInt(), (it.coord.y * 2).toInt(), 1, 1))
                    }
                    it.filter { (it as Hero).team == 3 }.forEach {
                        graphics.color = Color(255,0,0,50)
                        graphics.draw(Rectangle(((it as Hero).coord.x * 2).toInt(), (it.coord.y * 2).toInt(), 1, 1))
                    }
                }
        ImageIO.write(image, "PNG", File("image.png"));
    }

}
