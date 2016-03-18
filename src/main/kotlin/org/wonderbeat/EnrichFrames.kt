package org.wonderbeat

import org.slf4j.LoggerFactory
import skadistats.clarity.model.Entity
import skadistats.clarity.model.FieldPath
import skadistats.clarity.processor.entities.OnEntityCreated
import skadistats.clarity.processor.entities.OnEntityDeleted
import skadistats.clarity.processor.entities.OnEntityUpdated
import skadistats.clarity.processor.entities.UsesEntities
import skadistats.clarity.processor.reader.OnTickStart
import skadistats.clarity.processor.runner.Context
import skadistats.clarity.processor.runner.ControllableRunner
import skadistats.clarity.source.Source
import java.util.*

data class Frame(val created: ArrayList<Entity>, val updated: ArrayList<Entity>, val deleted: ArrayList<Entity>)

@UsesEntities
class FrameSource(val source: Source): Iterable<Frame> {

    private val logger = LoggerFactory.getLogger("org.wonderbeat.dotablame")

    val runner = ControllableRunner(source).runWith(this).let { it.seek(0); it }

    data class CurrentFrame(val created: ArrayList<Entity>, var updated: ArrayList<Entity>, var deleted: ArrayList<Entity>)
    private var currentFrame = CurrentFrame(arrayListOf(), arrayListOf(), arrayListOf())

    @OnTickStart
    fun onTickStart(ctx: Context, synthetic: Boolean) {
        currentFrame = CurrentFrame(arrayListOf(), arrayListOf(), arrayListOf())
    }

    @OnEntityCreated
    fun onCreate(ctx: Context, entity: Entity) {
        currentFrame.created.add(entity)
    }

    @OnEntityDeleted
    fun onDeleted(ctx: Context, entity: Entity) {
        currentFrame.deleted.add(entity)
    }

    @OnEntityUpdated
    fun onUpdate(ctx: Context, e: Entity, updatedPaths: Array<FieldPath>, updateCount: Int) {
        currentFrame.updated.add(e)
    }

    override fun iterator(): Iterator<Frame> {
        return object: Iterator<Frame> {
            override fun hasNext(): Boolean {
                return !runner.isAtEnd
            }

            override fun next(): Frame {
                runner.tick()
                return Frame(currentFrame.created, currentFrame.updated, currentFrame.deleted)
            }
        }
    }
}
