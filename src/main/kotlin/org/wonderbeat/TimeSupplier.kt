package org.wonderbeat

import org.funktionale.option.getOrElse
import org.funktionale.option.toOption
import org.joda.time.Duration


data class TimestampedFrame(val timestamp: Duration?, val frame: Frame)

fun Frame.toTimestamped() = TimestampedFrame(
            this.updated.find { it.hasProperty("m_pGameRules.m_fGameTime") }?.getProperty<Float>("m_pGameRules.m_fGameTime").toOption()
                    .getOrElse {
                        this.created.find { it.hasProperty("m_pGameRules.m_fGameTime") }?.getProperty<Float>("m_pGameRules.m_fGameTime")
                    }?.times(1000)?.toLong()?.let { Duration.millis(it) },
            this)
