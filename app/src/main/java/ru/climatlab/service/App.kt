package ru.climatlab.service

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
class App : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
    }
}