package com.tecfort.navigationtest.data.repository

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.FloorsClimbedRecord
import androidx.health.connect.client.records.PowerRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.tecfort.navigationtest.data.model.HealthMetrics
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

/**
 * [HealthConnectRepository] backed by the on-device Health Connect client.
 */
class DefaultHealthConnectRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : HealthConnectRepository {

    override val permissions: Set<String> = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ElevationGainedRecord::class),
        HealthPermission.getReadPermission(FloorsClimbedRecord::class),
        HealthPermission.getReadPermission(PowerRecord::class),
        HealthPermission.getReadPermission(SpeedRecord::class),
        HealthPermission.getReadPermission(CyclingPedalingCadenceRecord::class),
    )

    private val client: HealthConnectClient? by lazy {
        if (isAvailable()) HealthConnectClient.getOrCreate(context) else null
    }

    override fun isAvailable(): Boolean =
        HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE

    override suspend fun hasAllPermissions(): Boolean {
        val client = client ?: return false
        return client.permissionController.getGrantedPermissions().containsAll(permissions)
    }

    override suspend fun getTodayMetrics(): HealthMetrics {
        val client = client ?: return HealthMetrics()

        val zoneId = ZoneId.systemDefault()
        val now = Instant.now()
        val startOfDay = now.atZone(zoneId).toLocalDate().atStartOfDay(zoneId).toInstant()

        val response = client.aggregate(
            AggregateRequest(
                metrics = setOf(
                    StepsRecord.COUNT_TOTAL,
                    DistanceRecord.DISTANCE_TOTAL,
                    ElevationGainedRecord.ELEVATION_GAINED_TOTAL,
                    FloorsClimbedRecord.FLOORS_CLIMBED_TOTAL,
                    PowerRecord.POWER_AVG,
                    SpeedRecord.SPEED_AVG,
                    CyclingPedalingCadenceRecord.RPM_AVG,
                ),
                timeRangeFilter = TimeRangeFilter.between(startOfDay, now),
            ),
        )

        return HealthMetrics(
            steps = response[StepsRecord.COUNT_TOTAL] ?: 0L,
            distanceMeters = response[DistanceRecord.DISTANCE_TOTAL]?.inMeters,
            elevationGainedMeters = response[ElevationGainedRecord.ELEVATION_GAINED_TOTAL]?.inMeters,
            floorsClimbed = response[FloorsClimbedRecord.FLOORS_CLIMBED_TOTAL],
            averagePowerWatts = response[PowerRecord.POWER_AVG]?.inWatts,
            averageSpeedMetersPerSecond = response[SpeedRecord.SPEED_AVG]?.inMetersPerSecond,
            averageCadenceRpm = response[CyclingPedalingCadenceRecord.RPM_AVG],
        )
    }
}
