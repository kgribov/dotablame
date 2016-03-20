package org.wonderbeat

import org.joda.time.Duration
import skadistats.clarity.model.Entity
import java.util.*
import java.util.function.BiFunction

interface GameEntity
data class GameEntityFrame(val timestamp: Duration,
                           val created: List<GameEntity>,
                           val updated: List<GameEntity>,
                           val deleted: List<GameEntity>)

data class Coordinate(val x: Double, val y: Double)

fun <T> Boolean.then(funct: () -> T): T? = if(this == true) { funct() } else null

data class WardItem(val id: Int, val team: Int,  val type: WardType, val teamNum: Int): GameEntity
enum class WardType { OBSERVER, CENTRY }
fun Entity.extractWardItem(): WardItem? = this.dtClass.dtName.contains("ward", true)
        .and(this.dtClass.dtName.contains("item", true)).then {
    WardItem(
            this.serial,
            this.getProperty("m_iTeamNum"),
            when {
                this.dtClass.dtName.contains("observer", true) -> WardType.OBSERVER
                else -> WardType.CENTRY
            },
            this.getProperty("m_iTeamNum")
    )
}

data class WardNPC(val id: Int, val type: WardType, val teamNum: Int, val coord: Coordinate): GameEntity
fun Entity.extractWardNPC(): WardNPC? = this.dtClass.dtName.contains("ward", true)
        .and(this.dtClass.dtName.contains("NPC", true)).then {
    WardNPC(
            this.serial,
            when {
                this.dtClass.dtName.contains("observer", true) -> WardType.OBSERVER
                else -> WardType.CENTRY
            },
            this.getProperty("m_iTeamNum"),
            Coordinate(this.getProperty<Double>("CBodyComponent.m_cellX") + this.getProperty<Double>("CBodyComponent.m_vecX")/128, this
                    .getProperty<Double>("CBodyComponent.m_cellY") + this.getProperty<Double>("CBodyComponent.m_vecY")/128)
    )
}

data class Hero(val id: Int, val name: String, val level: Int, val health: Int, val mana: Int,
                val alive: Boolean, val isInvis: Boolean, val coord: Coordinate, val team: Int,
                val itemsOwnerId: Int): GameEntity
fun Entity.extractHero(): Hero? = this.dtClass.dtName.contains("unit", true)
        .and(this.dtClass.dtName.contains("hero", true)).then {
    Hero(
            this.serial,
            this.dtClass.dtName.substringAfterLast('_'),
            this.getProperty("m_iCurrentLevel"),
            this.getProperty("m_iHealth"),
            this.getProperty("m_flMana"),
            this.getProperty<Int>("m_lifeState") == 0,
            this.getProperty<Long>("m_nUnitState64").and(1L.shl(8)) != 0L,
            Coordinate(this.getProperty<Double>("CBodyComponent.m_cellX") + this.getProperty<Double>("CBodyComponent.m_vecX")/128, this
                    .getProperty<Double>("CBodyComponent.m_cellY") + this.getProperty<Double>("CBodyComponent.m_vecY")/128),
            this.getProperty("m_iTeamNum"),
            this.getProperty("m_hModifierParent")
    )
}

data class Item(val id: Int, val name: String, val itemsOwnerId: Int, val cooldown: Float, val charges: Int): GameEntity
fun Entity.extractItem(): Item? = (this.dtClass.dtName.contains("_Item_") &&
                                    this.getProperty<Int>("m_iTeamNum") != 4) // Rune or smth that environmental
        .then {
    Item(this.serial,
            this.dtClass.dtName.substringAfterLast('_'),
            this.getProperty("m_hOwnerEntity"),
            this.getProperty<Float>("m_flCooldownLength"),
            this.getProperty("m_iCurrentCharges")
            )
}

data class HeroWithInventory(val hero: Hero, val items: Set<Item>): GameEntity

class HeroAssembler {
    val heroToItems = HashMap<Hero, Set<Item>>()
    val ownerIdToHero = HashMap<Int, Hero>()

    fun assemble(frame: GameEntityFrame): GameEntityFrame {
        (frame.created + frame.updated).filterIsInstance<Hero>().forEach {
            heroToItems.computeIfAbsent(it, {setOf()})
            ownerIdToHero.put(it.itemsOwnerId, it)
        }
        (frame.created + frame.updated).filterIsInstance<Item>().forEach { item ->
            heroToItems.compute(ownerIdToHero[item.itemsOwnerId], BiFunction { key: Hero, oldVal: Set<Item> -> oldVal + item })
        }
        return GameEntityFrame(frame.timestamp, frame.created, frame.updated +
                heroToItems.map { HeroWithInventory(it.key, it.value) },
                frame.deleted)
    }
}
