package com.ilkeruzer.basketballheatmap.data.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.gson.annotations.SerializedName

/**
 * Created by İlker Üzer on 1/26/2021.
 * Copyright © 2021 İlker Üzer. All rights reserved.
 */

@Database(
        entities = [ShotDatabaseModel::class],
        version = 1,
        exportSchema = false
)
abstract class ShotDatabase : RoomDatabase() {

    companion object {
        fun create(context: Context, useInMemory: Boolean): ShotDatabase {
            val databaseBuilder = if (useInMemory) {
                Room.inMemoryDatabaseBuilder(context, ShotDatabase::class.java)
            } else {
                Room.databaseBuilder(
                        context,
                        ShotDatabase::class.java,
                        "shot.db"
                ) //TODO: always true in memory
            }
            return databaseBuilder
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }

    abstract fun shotDao(): ShotDao

}

@Dao
interface ShotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shots: List<ShotDatabaseModel>)

    @Query("DELETE FROM shot")
    suspend fun clearShots()

    @Query("SELECT count(*) say, shotPosX, shotPosY, id, ((SELECT count(*) FROM shot WHERE inOut=1 GROUP BY shotPosX, shotPosY order by id)*100)/count(*) as success from shot s GROUP by shotPosX,shotPosY order by success DESC")
    fun getAllShots() : LiveData<List<ShotFilterModel>>


    @Query("SELECT s.*,(count-success)as 'fail',((s.success*100)/s.count) as 'successRate' FROM (SELECT COUNT(*) as count, (select COUNT(*) FROM shot WHERE inOut = 1) as success FROM shot ) AS s")
    fun gerRateInfo(): LiveData<RateInfo>

}

@Entity(tableName = "shot")
data class ShotDatabaseModel (
        @PrimaryKey(autoGenerate = true) val id: Int? = null,
        @ColumnInfo(name ="shotId") val shotId: String,
        @ColumnInfo(name ="point") val point: Int?,
        @ColumnInfo(name ="segment") val segment: Int?,
        @ColumnInfo(name ="inOut") val inOut: Boolean,
        @ColumnInfo(name ="shotPosX") val shotPosX: Double?,
        @ColumnInfo(name = "shotPosY") val shotPosY: Double?
)

data class ShotFilterModel(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "say") val say: Int,
    @ColumnInfo(name ="shotPosX") val shotPosX: Double,
    @ColumnInfo(name = "shotPosY") val shotPosY: Double,
    @ColumnInfo(name = "success")  val success: Int,
)

data class RateInfo(
    @ColumnInfo(name = "count") val count: Int,
    @ColumnInfo(name = "success") val success: Int,
    @ColumnInfo(name = "fail") val fail: Int,
    @ColumnInfo(name = "successRate") val successRate: Int,
)