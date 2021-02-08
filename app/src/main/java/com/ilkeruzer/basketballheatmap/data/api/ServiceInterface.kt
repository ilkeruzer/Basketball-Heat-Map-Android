package com.ilkeruzer.basketballheatmap.data.api

import com.ilkeruzer.basketballheatmap.model.Response
import retrofit2.http.GET


/**
 * Created by İlker Üzer on 12/16/2020.
 * Copyright © 2020 İlker Üzer. All rights reserved.
 */
interface ServiceInterface {

    @GET("shots")
    suspend fun getData() : Response

}

