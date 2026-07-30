package com.tecfort.navigationtest.data.model

/**
 * Aggregated Health Connect activity totals for a single day. Power, speed, and cadence have no
 * meaningful "total" (they are instantaneous samples), so those are averages instead.
 */
data class HealthMetrics(
    val steps: Long = 0,
    val distanceMeters: Double? = null,
    val elevationGainedMeters: Double? = null,
    val floorsClimbed: Double? = null,
    val averagePowerWatts: Double? = null,
    val averageSpeedMetersPerSecond: Double? = null,
    val averageCadenceRpm: Double? = null,
)
