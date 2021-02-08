package com.ilkeruzer.basketballheatmap.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilkeruzer.basketballheatmap.data.api.NetworkModule
import com.ilkeruzer.basketballheatmap.data.local.RateInfo
import com.ilkeruzer.basketballheatmap.data.local.ShotDatabase
import com.ilkeruzer.basketballheatmap.data.local.ShotDatabaseModel
import com.ilkeruzer.basketballheatmap.data.local.ShotFilterModel
import com.ilkeruzer.basketballheatmap.model.Response
import com.ilkeruzer.basketballheatmap.model.Shot
import com.murgupluoglu.request.RESPONSE
import com.murgupluoglu.request.request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.round

class MainViewModel(
    private val networkModule: NetworkModule,
    private val db: ShotDatabase
    ): ViewModel() {

    val dataResponse: MutableLiveData<RESPONSE<Response>> =
        MutableLiveData()


    init {
        getAllData()
    }

    private fun getAllData() {
        dataResponse.request(viewModelScope, { networkModule.service().getData() })
    }

    fun shotInsertDb(shot: List<Shot>) = CoroutineScope(Dispatchers.IO).launch {
        db.shotDao().clearShots()
        val dbList = ArrayList<ShotDatabaseModel>()
        shot.filter{ shot -> shot.ShotPosX < 7.5 && shot.ShotPosY < 7.5 }
            .sortedWith(compareBy({ shot -> shot.ShotPosX }, { shot -> shot.ShotPosY }))
        .forEach {
            Log.d("TAG", "shotInsertDb: " + floor(it.ShotPosY))
            dbList.add(
                ShotDatabaseModel(
                shotId = it._id,
                point = it.point,
                inOut = it.InOut,
                segment = it.segment,
                shotPosX = round(it.ShotPosX),
                shotPosY = round(it.ShotPosY)
            )
            )
        }
        db.shotDao().insertAll(dbList)
    }

     fun getFilterShot(): LiveData<List<ShotFilterModel>> {
        return db.shotDao().getAllShots()
    }

    fun getRateInfo(): LiveData<RateInfo> {
        return db.shotDao().gerRateInfo()
    }
}