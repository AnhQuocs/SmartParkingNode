package com.example.smarttrafficradar.features.dashboard.data.repository

import com.example.smarttrafficradar.features.dashboard.domain.model.ParkingAnalytics
import com.example.smarttrafficradar.features.dashboard.domain.model.ParkingSummary
import com.example.smarttrafficradar.features.dashboard.domain.repository.AnalyticsRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsRepositoryImpl(
    private val database: FirebaseDatabase
) : AnalyticsRepository {

    override fun getParkingSummary(): Flow<ParkingSummary> = callbackFlow {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(Date())
        
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = sdf.format(calendar.time)

        val ref = database.getReference("parking_analytics")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val todayData = snapshot.child(todayStr).let {
                    ParkingAnalytics(
                        todayInCount = it.child("today_in_count").getValue(Int::class.java) ?: 0,
                        todayOutCount = it.child("today_out_count").getValue(Int::class.java) ?: 0,
                        todayRevenue = it.child("today_revenue").getValue(Long::class.java) ?: 0L,
                        vehiclesInLot = it.child("vehicles_in_lot").getValue(Int::class.java) ?: 0
                    )
                }
                
                val yesterdayRevenue = snapshot.child(yesterdayStr).child("today_revenue").getValue(Long::class.java) ?: 0L
                
                val revenueChangePercentage = if (yesterdayRevenue > 0) {
                    ((todayData.todayRevenue - yesterdayRevenue).toDouble() / yesterdayRevenue) * 100
                } else if (todayData.todayRevenue > 0) {
                    100.0
                } else {
                    0.0
                }

                trySend(ParkingSummary(todayData, revenueChangePercentage))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
