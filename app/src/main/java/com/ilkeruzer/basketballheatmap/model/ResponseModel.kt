package com.ilkeruzer.basketballheatmap.model

data class Response(
    val success: Boolean,
    val data : List<Data>
)

data class Data(
    val user: User,
    val shots: List<Shot>
)

data class User(
    val name: String,
    val surname: String
)

data class Shot(
    val point: Int,
    val segment: Int,
    val _id: String,
    val InOut: Boolean,
    val ShotPosX: Double,
    val ShotPosY: Double
)