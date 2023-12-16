package com.example.m5.logic.network

import com.example.m5.logic.model.HighQualityResponse
import com.example.m5.logic.model.HotMusicListResponse
import com.example.m5.logic.model.NewMusicesAls
import retrofit2.Call
import retrofit2.http.GET


interface MainService {

    @GET("/top/playlist/highquality?limit=6")
    fun mainHighQuality(): Call<HighQualityResponse>

    @GET("/search/hot/detail")
    fun mainHotMusic(): Call<HotMusicListResponse>

    @GET("/top/song?type=7")
    fun mainNewSongAl(): Call<NewMusicesAls>

}