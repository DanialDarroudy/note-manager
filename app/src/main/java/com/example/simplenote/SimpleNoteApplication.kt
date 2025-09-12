package com.example.simplenote

import android.app.Application
import com.example.simplenote.core.dependencyinjection.DependencyProvider

class SimpleNoteApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyProvider.init(applicationContext)
    }
}