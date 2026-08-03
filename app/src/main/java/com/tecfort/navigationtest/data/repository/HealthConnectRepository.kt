package com.tecfort.navigationtest.data.repository

import com.tecfort.navigationtest.data.model.HealthMetrics
import java.time.LocalDate

/**
 * Source of truth for the Health Connect activity data shown on the Favorite screen.
 */
interface HealthConnectRepository {

    /** Read permissions this repository needs; pass to [PermissionController] request flows. */
    val permissions: Set<String>

    /** Whether the Health Connect SDK/provider is usable on this device. */
    fun isAvailable(): Boolean

    /** Whether all of [permissions] have already been granted. */
    suspend fun hasAllPermissions(): Boolean

    /** Aggregated totals for today, from midnight to now. */
    suspend fun getTodayMetrics(): HealthMetrics

    /** Aggregated totals for [date], from midnight to midnight of the next day. */
    suspend fun getMetricsForDate(date: LocalDate): HealthMetrics

    /** Aggregated totals across [startDate]..[endDate] (inclusive). */
    suspend fun getMetricsForRange(startDate: LocalDate, endDate: LocalDate): HealthMetrics
}
