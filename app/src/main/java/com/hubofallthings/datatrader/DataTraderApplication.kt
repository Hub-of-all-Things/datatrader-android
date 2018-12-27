package com.hubofallthings.datatrader

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class DataTraderApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
    }
}