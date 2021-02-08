package com.ilkeruzer.basketballheatmap.di

import com.ilkeruzer.basketballheatmap.data.api.NetworkModule
import com.ilkeruzer.basketballheatmap.data.local.ShotDatabase
import com.ilkeruzer.basketballheatmap.view.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by İlker Üzer on 12/15/2020.
 * Copyright © 2020 İlker Üzer. All rights reserved.
 */
val appModule = module {
    single { NetworkModule() }
    single { ShotDatabase.create(androidApplication(),true) }
}


val viewModelModule = module {
   viewModel { MainViewModel(get(),get()) }
}