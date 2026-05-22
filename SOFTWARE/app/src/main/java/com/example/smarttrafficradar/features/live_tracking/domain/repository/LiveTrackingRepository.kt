<<<<<<< HEAD
package com.example.smarttrafficradar.features.live_tracking.domain.repository

import com.example.smarttrafficradar.features.live_tracking.domain.model.LiveTracking
import kotlinx.coroutines.flow.Flow

interface LiveTrackingRepository {
    fun observeLiveTracking(nodeId: String): Flow<LiveTracking>
=======
package com.example.smarttrafficradar.features.live_tracking.domain.repository

import com.example.smarttrafficradar.features.live_tracking.domain.model.LiveTracking
import kotlinx.coroutines.flow.Flow

interface LiveTrackingRepository {
    fun observeLiveTracking(nodeId: String): Flow<LiveTracking>
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}