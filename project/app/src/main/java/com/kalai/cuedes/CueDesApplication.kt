package com.kalai.cuedes

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.kalai.cuedes.data.AlarmDatabase
import com.kalai.cuedes.data.AlarmRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.DebugTree

class CueDesApplication: Application() {

    companion object{
        const val CHANNEL_ID = "562104"
    }


    private val database by lazy {  AlarmDatabase.getDatabase(this)  }
    val repository by lazy { AlarmRepository(database.alarmDao()) }
    val geofencingClient: GeofencingClient by lazy {  LocationServices.getGeofencingClient(this) }
    private val cueDesServiceIntent  by lazy {  Intent(this,CueDesService::class.java) }

    private val fusedLocationClient: FusedLocationProviderClient  by lazy { LocationServices.getFusedLocationProviderClient(this) }


    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
        createNotificationChannel()
        GlobalScope.launch {
            repository.alarms.collect { alarms ->
                if(alarms.any { alarm -> alarm.isActivated  }){
                    startService(cueDesServiceIntent)
                }
                else
                    stopService(cueDesServiceIntent)
            }
        }

    }





    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Timber.d("Notification channel created")
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText

            }

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}

