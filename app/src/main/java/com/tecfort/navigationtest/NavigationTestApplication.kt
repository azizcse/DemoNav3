package com.tecfort.navigationtest

import android.app.Application
import com.tecfort.navigationtest.service.EventTrackerService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NavigationTestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        EventTrackerService.start(this)
    }
}
