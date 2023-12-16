package com.example.m5.logic.network

import com.example.m5.logic.model.RecommendSongsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RecommendService {

    //获取每日推荐
    @GET("/recommend/songs")
    fun getRecommend(@Query("cookie")cookie: String): Call<RecommendSongsResponse>

}