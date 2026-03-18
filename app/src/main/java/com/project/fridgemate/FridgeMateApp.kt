package com.project.fridgemate

import android.app.Application
import com.project.fridgemate.data.local.AppDatabase
import com.project.fridgemate.data.remote.ApiClient

class FridgeMateApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ApiClient.init(this)
        AppDatabase.getInstance(this)
    }
}
