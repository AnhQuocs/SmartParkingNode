<<<<<<< HEAD
package com.example.smarttrafficradar.features.live_tracking.domain.usecase

import com.example.smarttrafficradar.features.live_tracking.domain.model.LiveTracking
import com.example.smarttrafficradar.features.live_tracking.domain.repository.LiveTrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLiveTrackingUseCase @Inject constructor(
    val repository: LiveTrackingRepository
) {
    operator fun invoke(nodeId: String): Flow<LiveTracking> {
        return repository.observeLiveTracking(nodeId)
    }
=======
package com.example.smarttrafficradar.features.live_tracking.domain.usecase

import com.example.smarttrafficradar.features.live_tracking.domain.model.LiveTracking
import com.example.smarttrafficradar.features.live_tracking.domain.repository.LiveTrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLiveTrackingUseCase @Inject constructor(
    val repository: LiveTrackingRepository
) {
    operator fun invoke(nodeId: String): Flow<LiveTracking> {
        return repository.observeLiveTracking(nodeId)
    }
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}