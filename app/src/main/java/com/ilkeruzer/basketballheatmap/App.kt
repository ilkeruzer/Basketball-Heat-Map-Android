package com.ilkeruzer.basketballheatmap

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.ilkeruzer.basketballheatmap.di.appModule
import com.ilkeruzer.basketballheatmap.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Created by İlker Üzer on 12/15/2020.
 * Copyright © 2020 İlker Üzer. All rights reserved.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            modules(listOf(appModule, viewModelModule))
        }
    }

}